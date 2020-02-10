package com.plutonem.ui.nemur;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.plutonem.Plutonem;
import com.plutonem.R;
import com.plutonem.android.fluxc.Dispatcher;
import com.plutonem.datasets.NemurDatabase;
import com.plutonem.datasets.NemurOrderTable;
import com.plutonem.datasets.NemurTagTable;
import com.plutonem.models.FilterCriteria;
import com.plutonem.models.NemurOrder;
import com.plutonem.models.NemurTag;
import com.plutonem.models.NemurTagList;
import com.plutonem.models.news.NewsItem;
import com.plutonem.ui.ActionableEmptyView;
import com.plutonem.ui.EmptyViewMessageType;
import com.plutonem.ui.FilteredRecyclerView;
import com.plutonem.ui.main.MainToolbarFragment;
import com.plutonem.ui.main.PMainActivity;
import com.plutonem.ui.nemur.NemurTypes.NemurOrderListType;
import com.plutonem.ui.nemur.actions.NemurActions;
import com.plutonem.ui.nemur.services.order.NemurOrderServiceStarter;
import com.plutonem.ui.nemur.services.order.NemurOrderServiceStarter.UpdateAction;
import com.plutonem.ui.nemur.services.update.NemurUpdateLogic.UpdateTask;
import com.plutonem.ui.nemur.services.update.NemurUpdateServiceStarter;
import com.plutonem.ui.nemur.utils.NemurUtils;
import com.plutonem.ui.nemur.viewmodels.NemurOrderListViewModel;
import com.plutonem.ui.nemur.views.NemurBuyerHeaderView;
import com.plutonem.ui.news.NewsViewHolder.NewsCardListener;
import com.plutonem.ui.prefs.AppPrefs;
import com.plutonem.utilities.AniUtils;
import com.plutonem.utilities.PNActivityUtils;
import com.plutonem.utilities.image.ImageManager;
import com.plutonem.widgets.RecyclerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;
import org.wordpress.android.util.DateTimeUtils;
import org.wordpress.android.util.NetworkUtils;
import org.wordpress.android.util.ToastUtils;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.inject.Inject;

import kohii.v1.core.MemoryMode;
import kohii.v1.exoplayer.Kohii;

public class NemurOrderListFragment extends Fragment
        implements NemurInterfaces.OnOrderSelectedListener,
        PMainActivity.OnScrollToTopListener,
        MainToolbarFragment {
    private NemurOrderAdapter mOrderAdapter;

    private FilteredRecyclerView mRecyclerView;
    private boolean mFirstLoad = true;

    private View mNewOrdersBar;
    private ActionableEmptyView mActionableEmptyView;
    private ProgressBar mProgress;

    private boolean mIsTopLevel = false;

    private NemurTag mCurrentTag;
    private NemurOrderListType mOrderListType;

    private int mRestorePosition;

    private boolean mIsUpdating;
    private boolean mWasPaused;
    private boolean mHasUpdatedOrders;
    private boolean mIsAnimatingOutNewOrdersBar;

    private static boolean mHasPurgedNemurDb;
    private static Date mLastAutoUpdateDt;

    private NemurOrderListViewModel mViewModel;

    private Kohii kohii;

    private Observer<NewsItem> mNewsItemObserver = new Observer<NewsItem>() {
        @Override
        public void onChanged(@Nullable NewsItem newsItem) {
            getOrderAdapter().updateNewsCardItem(newsItem);
        }
    };

    @Inject ViewModelProvider.Factory mViewModelFactory;
    @Inject ImageManager mImageManager;
    @Inject Dispatcher mDispatcher;

    private enum ActionableEmptyViewButtonType {}

    public static NemurOrderListFragment newInstance(boolean isTopLevel) {
        NemurTag tag = AppPrefs.getNemurTag();
        if (tag == null) {
            tag = NemurUtils.getDefaultTag();
        }
        return newInstanceForTag(tag, NemurOrderListType.TAG_DEFAULT, isTopLevel);
    }

    static NemurOrderListFragment newInstanceForTag(NemurTag tag, NemurOrderListType listType, boolean isTopLevel) {
        AppLog.d(T.NEMUR, "nemur order list > newInstance (tag)");

        Bundle args = new Bundle();
        args.putSerializable(NemurConstants.ARG_TAG, tag);
        args.putSerializable(NemurConstants.ARG_ORDER_LIST_TYPE, listType);
        args.putBoolean(NemurConstants.ARG_IS_TOP_LEVEL, isTopLevel);

        NemurOrderListFragment fragment = new NemurOrderListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        if (args != null) {
            if (args.containsKey(NemurConstants.ARG_TAG)) {
                mCurrentTag = (NemurTag) args.getSerializable(NemurConstants.ARG_TAG);
            }
            if (args.containsKey(NemurConstants.ARG_ORDER_LIST_TYPE)) {
                mOrderListType = (NemurOrderListType) args.getSerializable(NemurConstants.ARG_ORDER_LIST_TYPE);
            }

            if (args.containsKey(NemurConstants.ARG_IS_TOP_LEVEL)) {
                mIsTopLevel = args.getBoolean(NemurConstants.ARG_IS_TOP_LEVEL);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Plutonem) getActivity().getApplication()).component().inject(this);

        if (savedInstanceState != null) {
            AppLog.d(T.NEMUR, "nemur order list > restoring instance state");
            if (savedInstanceState.containsKey(NemurConstants.ARG_TAG)) {
                mCurrentTag = (NemurTag) savedInstanceState.getSerializable(NemurConstants.ARG_TAG);
            }
            if (savedInstanceState.containsKey(NemurConstants.ARG_ORDER_LIST_TYPE)) {
                mOrderListType =
                        (NemurOrderListType) savedInstanceState.getSerializable(NemurConstants.ARG_ORDER_LIST_TYPE);
            }
            if (savedInstanceState.containsKey(NemurConstants.ARG_IS_TOP_LEVEL)) {
                mIsTopLevel = savedInstanceState.getBoolean(NemurConstants.ARG_IS_TOP_LEVEL);
            }

            mRestorePosition = savedInstanceState.getInt(NemurConstants.KEY_RESTORE_POSITION);
            mWasPaused = savedInstanceState.getBoolean(NemurConstants.KEY_WAS_PAUSED);
            mHasUpdatedOrders = savedInstanceState.getBoolean(NemurConstants.KEY_ALREADY_UPDATED);
            mFirstLoad = savedInstanceState.getBoolean(NemurConstants.KEY_FIRST_LOAD);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // we need to pass activity, since this fragment extends Android Native fragment (we can pass `this` as soon as
        // this fragment extends Support fragment.
        mViewModel = ViewModelProviders.of((FragmentActivity) getActivity(), mViewModelFactory)
                .get(NemurOrderListViewModel.class);

        mViewModel.start(
                mCurrentTag,
                false
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        mWasPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkOrderAdapter();

        if (mWasPaused) {
            AppLog.d(T.NEMUR, "nemur order list > resumed from paused state");
            mWasPaused = false;

            if (getOrderListType() == NemurOrderListType.TAG_DEFAULT) {
                resumeDefaultTag();
            } else {
                refreshOrders();
            }

            NemurTag nemurTag = NemurUtils.getTagFromEndpoint(NemurTag.NEMUR_PATH);
            NemurTag nemNemurTag = AppPrefs.getNemurTag();

            if (nemurTag != null && nemurTag.equals(nemNemurTag)) {
                setCurrentTag(nemNemurTag);
                updateCurrentTag();
            } else if (nemurTag == null) {
                AppLog.w(T.NEMUR, "Nemur tag not found; NemurTagTable returned null");
            }
        }
    }

    /*
     * called when fragment is resumed and we're looking at orders in a default tag
     */
    private void resumeDefaultTag() {
        if (!NemurTagTable.tagExists(getCurrentTag())) {
            // current tag no longer exists, revert to default
            AppLog.d(T.NEMUR, "nemur order list > current tag no longer valid");
            NemurTag tag = NemurUtils.getDefaultTag();
            // it's possible the default tag won't exist if the user just changed the app's
            // language, in which case default to the first tag in the table
            if (!NemurTagTable.tagExists(tag)) {
                tag = NemurTagTable.getFirstTag();
            }
            setCurrentTag(tag);
        } else {
            // otherwise, refresh orders to make sure any changes are reflected and auto-update
            // orders in the current tag if it's time
            refreshOrders();
            updateCurrentTagIfTime();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mDispatcher.register(this);
        EventBus.getDefault().register(this);

        reloadTags();

        // purge database and update default tags/buyer if necessary - note that we don't purge unless
        // there's a connection to avoid removing orders the user would expect to see offline
        if (getOrderListType() == NemurOrderListType.TAG_DEFAULT && NetworkUtils.isNetworkAvailable(getActivity())) {
            purgeDatabaseIfNeeded();
            updateDefaultTagsIfNeeded();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mDispatcher.unregister(this);
        EventBus.getDefault().unregister(this);
    }

    /*
     * ensures the adapter is created and orders are updated if they haven't already been
     */
    private void checkOrderAdapter() {
        if (isAdded() && mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(getOrderAdapter());

            if (!mHasUpdatedOrders && NetworkUtils.isNetworkAvailable(getActivity())) {
                mHasUpdatedOrders = true;
                if (getOrderListType().isTagType()) {
                    updateCurrentTagIfTime();
                }
            }
        }
    }

    @Override
    public void setTitle(@NonNull String title) {
        // Do nothing - no title for this toolbar
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NemurEvents.DefaultTagsChanged event) {
        if (getOrderListType() == NemurOrderListType.TAG_DEFAULT) {
            // reload the tag filter since tags have changed
            reloadTags();

            // update the current tag if the list fragment is empty - this will happen if
            // the tag table was previously empty (ie: first run)
            if (isOrderAdapterEmpty()) {
                updateCurrentTag();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        AppLog.d(T.NEMUR, "nemur order list > saving instance state");

        if (mCurrentTag != null) {
            outState.putSerializable(NemurConstants.ARG_TAG, mCurrentTag);
        }

        outState.putBoolean(NemurConstants.KEY_WAS_PAUSED, mWasPaused);
        outState.putBoolean(NemurConstants.KEY_ALREADY_UPDATED, mHasUpdatedOrders);
        outState.putBoolean(NemurConstants.KEY_FIRST_LOAD, mFirstLoad);
        outState.putBoolean(NemurConstants.KEY_IS_REFRESHING, mRecyclerView.isRefreshing());
        outState.putInt(NemurConstants.KEY_RESTORE_POSITION, getCurrentPosition());
        outState.putSerializable(NemurConstants.ARG_ORDER_LIST_TYPE, getOrderListType());
        outState.putBoolean(NemurConstants.ARG_IS_TOP_LEVEL, mIsTopLevel);

        super.onSaveInstanceState(outState);
    }

    private void updateOrders(boolean forced) {
        if (!isAdded()) {
            return;
        }

        if (!NetworkUtils.checkConnection(getActivity())) {
            mRecyclerView.setRefreshing(false);
            return;
        }

        if (mFirstLoad) {
            // let onResume() take care of this logic, as the FilteredRecyclerView.FilterListener onLoadData
            // method is called on two moments: once for first time load, and then each time the swipe to
            // refresh gesture triggers a refresh.
            mRecyclerView.setRefreshing(false);
            mFirstLoad = false;
        } else {
            UpdateAction updateAction = forced ? UpdateAction.REQUEST_REFRESH
                    : UpdateAction.REQUEST_NEWER;
            switch (getOrderListType()) {
                case TAG_DEFAULT:
                    updateOrdersWithTag(getCurrentTag(), updateAction);
                    break;
            }
            // make sure swipe-to-refresh progress shows since this is a manual refresh
            mRecyclerView.setRefreshing(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.nemur_fragment_order_cards, container, false);
        mRecyclerView = rootView.findViewById(R.id.nemur_recycler_view);

        Context context = container.getContext();

        mActionableEmptyView = rootView.findViewById(R.id.empty_custom_view);
        mRecyclerView.setLogT(AppLog.T.NEMUR);
        mRecyclerView.setCustomEmptyView(mActionableEmptyView);
        mRecyclerView.setFilterListener(new FilteredRecyclerView.FilterListener() {
            @Override
            public List<FilterCriteria> onLoadFilterCriteriaOptions(boolean refresh) {
                return null;
            }

            @Override
            public void onLoadFilterCriteriaOptionsAsync(
                    FilteredRecyclerView.FilterCriteriaAsyncLoaderListener listener, boolean refresh) {
                loadTags(listener);
            }

            @Override
            public void onLoadData(boolean forced) {
                updateOrders(forced);
            }

            @Override
            public void onFilterSelected(int position, FilterCriteria criteria) {
                onTagChanged((NemurTag) criteria);
            }

            @Override
            public FilterCriteria onRecallSelection() {
                if (hasCurrentTag()) {
                    return getCurrentTag();
                } else {
                    AppLog.w(T.NEMUR, "nemur order list > no current tag in onRecallSelection");
                    return NemurUtils.getDefaultTag();
                }
            }

            @Override
            public String onShowEmptyViewMessage(EmptyViewMessageType emptyViewMsgType) {
                return null;
            }

            @Override
            public void onShowCustomEmptyView(EmptyViewMessageType emptyViewMsgType) {
                setEmptyTitleDescriptionAndButton(
                        EmptyViewMessageType.NETWORK_ERROR.equals(emptyViewMsgType)
                                || EmptyViewMessageType.GENERIC_ERROR.equals(emptyViewMsgType));
            }
        });

        // add the item decoration (dividers) to the recycler, skipping the first item if the first
        // item is the tag toolbar (shown when viewing posts in followed tags) - this is to avoid
        // having the tag toolbar take up more vertical space than necessary
        int spacingHorizontal = context.getResources().getDimensionPixelSize(R.dimen.nemur_card_margin);
        int spacingVertical = context.getResources().getDimensionPixelSize(R.dimen.nemur_card_gutters);
        mRecyclerView.addItemDecoration(new RecyclerItemDecoration(spacingHorizontal, spacingVertical, false));

        // the following will change the look and feel of the toolbar to match the current design
        mRecyclerView.setToolbarBackgroundColor(ContextCompat.getColor(context, R.color.primary));
        mRecyclerView.setToolbarSpinnerTextColor(ContextCompat.getColor(context, android.R.color.white));
        mRecyclerView.setToolbarSpinnerDrawable(R.drawable.ic_dropdown_primary_30_24dp);

        mRecyclerView.setToolbarLeftAndRightPadding(
                getResources().getDimensionPixelSize(R.dimen.margin_medium),
                getResources().getDimensionPixelSize(R.dimen.margin_extra_large));

        mRecyclerView.setSwipeToRefreshEnabled(true);

        // bar that appears at top after new posts are loaded
        mNewOrdersBar = rootView.findViewById(R.id.layout_new_orders);
        mNewOrdersBar.setVisibility(View.GONE);
        mNewOrdersBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.scrollRecycleViewToPosition(0);
                refreshOrders();
            }
        });

        // progress bar that appears when loading more posts
        mProgress = rootView.findViewById(R.id.progress_footer);
        mProgress.setVisibility(View.GONE);

        if (savedInstanceState != null && savedInstanceState.getBoolean(NemurConstants.KEY_IS_REFRESHING)) {
            mIsUpdating = true;
            mRecyclerView.setRefreshing(true);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        kohii = Kohii.get(this);
        kohii.register(this, MemoryMode.LOW)
                .addBucket(this.mRecyclerView);
    }

    private int getCurrentPosition() {
        if (mRecyclerView != null && hasOrderAdapter()) {
            return mRecyclerView.getCurrentPosition();
        } else {
            return -1;
        }
    }

    private int getEmptyViewTopMargin() {
        int totalMargin = getActivity().getResources().getDimensionPixelSize(R.dimen.wordpress_toolbar_height);

        return totalMargin;
    }

    private void setEmptyTitleDescriptionAndButton(boolean requestFailed) {
        if (!isAdded()) {
            return;
        }

        mActionableEmptyView.updateLayoutForSearch(false, getEmptyViewTopMargin());
        String title;
        String description = null;
        ActionableEmptyViewButtonType button = null;

        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            title = getString(R.string.nemur_empty_orders_no_connection);
        } else if (requestFailed) {
            title = getString(R.string.nemur_empty_orders_request_failed);
        } else if (isUpdating()) {
            title = getString(R.string.nemur_empty_orders_in_tag_updating);
        } else {
            switch (getOrderListType()) {
                case TAG_DEFAULT:
                    title = getString(R.string.nemur_empty_orders_in_tag);
                    break;
                default:
                    title = getString(R.string.nemur_empty_orders_in_tag);
                    break;
            }
        }

        setEmptyTitleDescriptionAndButton(title, description, button);
    }

    private void setEmptyTitleDescriptionAndButton(@NonNull String title, String description,
                                                   final ActionableEmptyViewButtonType button) {
        if (!isAdded()) {
            return;
        }

        mActionableEmptyView.image.setVisibility(!isUpdating() ? View.VISIBLE : View.GONE);
        mActionableEmptyView.title.setText(title);

        if (description == null) {
            mActionableEmptyView.subtitle.setVisibility(View.GONE);
        } else {
            mActionableEmptyView.subtitle.setVisibility(View.VISIBLE);

            if (description.contains("<") && description.contains(">")) {
                mActionableEmptyView.subtitle.setText(Html.fromHtml(description));
            } else {
                mActionableEmptyView.subtitle.setText(description);
            }
        }

        if (button == null) {
            mActionableEmptyView.button.setVisibility(View.GONE);
        } else {
            mActionableEmptyView.button.setVisibility(View.VISIBLE);
        }
    }

    private void showEmptyView() {
        if (isAdded()) {
            mActionableEmptyView.setVisibility(View.VISIBLE);
            mActionableEmptyView.announceEmptyStateForAccessibility();
        }
    }

    private void hideEmptyView() {
        if (isAdded()) {
            mActionableEmptyView.setVisibility(View.GONE);
        }
    }

    /*
     * called by post adapter when data has been loaded
     */
    private final NemurInterfaces.DataLoadedListener mDataLoadedListener = new NemurInterfaces.DataLoadedListener() {
        @Override
        public void onDataLoaded(boolean isEmpty) {
            if (!isAdded()) {
                return;
            }
            if (isEmpty) {
                setEmptyTitleDescriptionAndButton(false);
                showEmptyView();
            } else {
                hideEmptyView();
                announceListStateForAccessibility();
                if (mRestorePosition > 0) {
                    AppLog.d(T.NEMUR, "nemur order list > restoring position");
                    mRecyclerView.scrollRecycleViewToPosition(mRestorePosition);
                }
            }
            mRestorePosition = 0;
        }
    };

    private void announceListStateForAccessibility() {
        if (getView() != null) {
            getView().announceForAccessibility(getString(R.string.nemur_acessibility_list_loaded,
                    getOrderAdapter().getItemCount()));
        }
    }

    /*
     * called by order adapter to load older orders when user scrolls to the last order
     */
    private final NemurActions.DataRequestedListener mDataRequestedListener =
            new NemurActions.DataRequestedListener() {
                @Override
                public void onRequestData() {
                    // skip if update is already in progress
                    if (isUpdating()) {
                        return;
                    }

                    // request older posts unless we already have the max # to show
                    switch (getOrderListType()) {
                        case TAG_DEFAULT:
                            if (NemurOrderTable.getNumOrdersWithTag(mCurrentTag)
                                    < NemurConstants.NEMUR_MAX_ORDERS_TO_DISPLAY) {
                                // request older orders
                                updateOrdersWithTag(getCurrentTag(), UpdateAction.REQUEST_OLDER);
                            }
                            break;
                    }
                }
            };

    private final NewsCardListener mNewsCardListener = new NewsCardListener() {
        @Override public void onItemShown(@NotNull NewsItem item) {
            mViewModel.onNewsCardShown(item, getCurrentTag());
        }

        @Override public void onItemClicked(@NotNull NewsItem item) { }

        @Override public void onDismissClicked(NewsItem item) {
            mViewModel.onNewsCardDismissed(item);
        }
    };

    private NemurOrderAdapter getOrderAdapter() {
        if (mOrderAdapter == null) {
            AppLog.d(T.NEMUR, "nemur order list > creating order adapter");
            Context context = PNActivityUtils.getThemedContext(getActivity());
            mOrderAdapter = new NemurOrderAdapter(
                    context,
                    getOrderListType(),
                    mImageManager,
                    kohii
            );
            mOrderAdapter.setOnOrderSelectedListener(this);
            mOrderAdapter.setOnDataLoadedListener(mDataLoadedListener);
            mOrderAdapter.setOnDataRequestedListener(mDataRequestedListener);
            mOrderAdapter.setOnNewsCardListener(mNewsCardListener);
            if (getActivity() instanceof NemurBuyerHeaderView.OnBuyerInfoLoadedListener) {
                mOrderAdapter.setOnBuyerInfoLoadedListener((NemurBuyerHeaderView.OnBuyerInfoLoadedListener) getActivity());
            }
            mViewModel.getNewsDataSource().removeObserver(mNewsItemObserver);
            if (getOrderListType().isTagType()) {
                mOrderAdapter.setCurrentTag(getCurrentTag());
                mViewModel.getNewsDataSource().observe((FragmentActivity) getActivity(), mNewsItemObserver);
            }
        }
        return mOrderAdapter;
    }

    private boolean hasOrderAdapter() {
        return (mOrderAdapter != null);
    }

    private boolean isOrderAdapterEmpty() {
        return (mOrderAdapter == null || mOrderAdapter.isEmpty());
    }

    private boolean isCurrentTag(final NemurTag tag) {
        return NemurTag.isSameTag(tag, mCurrentTag);
    }

    private NemurTag getCurrentTag() {
        return mCurrentTag;
    }

    private boolean hasCurrentTag() {
        return mCurrentTag != null;
    }

    private void setCurrentTag(final NemurTag tag) {
        if (tag == null) {
            return;
        }

        // skip if this is already the current tag and the post adapter is already showing it
        if (isCurrentTag(tag)
                && hasOrderAdapter()
                && getOrderAdapter().isCurrentTag(tag)) {
            return;
        }

        mCurrentTag = tag;

        mViewModel.onTagChanged(mCurrentTag);

        switch (getOrderListType()) {
            case TAG_DEFAULT:
                // remember this as the current tag if viewing default tag
                AppPrefs.setNemurTag(tag);

                break;
        }

        getOrderAdapter().setCurrentTag(mCurrentTag);
        hideNewOrdersBar();
        showLoadingProgress(false);
        updateCurrentTagIfTime();
    }

    /*
     * load tags on which the main data will be filtered
     */
    private void loadTags(FilteredRecyclerView.FilterCriteriaAsyncLoaderListener listener) {
        new LoadTagsTask(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /*
     * refresh adapter so latest orders appear
     */
    private void refreshOrders() {
        hideNewOrdersBar();
        if (hasOrderAdapter()) {
            getOrderAdapter().refresh();
        }
    }

    /*
     * same as above but clears orders before refreshing
     */
    private void reloadOrders() {
        hideNewOrdersBar();
        if (hasOrderAdapter()) {
            getOrderAdapter().reload();
        }
    }

    /*
     * reload the list of tags for the dropdown filter
     */
    private void reloadTags() {
        if (isAdded() && mRecyclerView != null) {
            mRecyclerView.refreshFilterCriteriaOptions();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NemurEvents.UpdateOrdersStarted event) {
        if (!isAdded()) {
            return;
        }

        setIsUpdating(true, event.getAction());
        setEmptyTitleDescriptionAndButton(false);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(NemurEvents.UpdateOrdersEnded event) {
        if (!isAdded()) {
            return;
        }

        setIsUpdating(false, event.getAction());
        if (event.getNemurTag() != null && !isCurrentTag(event.getNemurTag())) {
            return;
        }

        // determine whether to show the "new orders" bar - when this is shown, the newly
        // downloaded orders aren't displayed until the user taps the bar - only appears
        // when there are new orders in a default tag and the user has scrolled the list
        // beyond the first order
        if (event.getResult() == NemurActions.UpdateResult.HAS_NEW
                && event.getAction() == UpdateAction.REQUEST_NEWER
                && getOrderListType() == NemurOrderListType.TAG_DEFAULT
                && !isOrderAdapterEmpty()
                && (!isAdded() || !mRecyclerView.isFirstItemVisible())) {
            showNewOrdersBar();
        } else if (event.getResult().isNewOrChanged()
                || event.getAction() == UpdateAction.REQUEST_REFRESH) {
            refreshOrders();
        } else {
            boolean requestFailed = (event.getResult() == NemurActions.UpdateResult.FAILED);
            setEmptyTitleDescriptionAndButton(requestFailed);
            // if we requested orders in order to fill a gap but the request failed or didn't
            // return any orders, reload the adapter so the gap marker is reset (hiding its
            // progress bar)
            if (event.getAction() == UpdateAction.REQUEST_OLDER_THAN_GAP) {
                reloadOrders();
            }
        }
    }

    /*
     * get latest orders for this tag from the server
     */
    private void updateOrdersWithTag(NemurTag tag, UpdateAction updateAction) {
        if (!isAdded()) {
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            AppLog.i(T.NEMUR, "nemur order list > network unavailable, canceled tag update");
            return;
        }
        if (tag == null) {
            AppLog.w(T.NEMUR, "null tag passed to updateOrdersWithTag");
            return;
        }
        AppLog.d(T.NEMUR,
                "nemur order list > updating tag " + tag.getTagNameForLog() + ", updateAction=" + updateAction.name());
        NemurOrderServiceStarter.startServiceForTag(getActivity(), tag, updateAction);
    }


    private void updateCurrentTag() {
        updateOrdersWithTag(getCurrentTag(), UpdateAction.REQUEST_NEWER);
    }

    /*
     * update the current tag if it's time to do so - note that the check is done in the
     * background since it can be expensive and this is called when the fragment is
     * resumed, which on slower devices can result in a janky experience
     */
    private void updateCurrentTagIfTime() {
        if (!isAdded() || !hasCurrentTag()) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                if (NemurTagTable.shouldAutoUpdateTag(getCurrentTag()) && isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCurrentTag();
                        }
                    });
                }
            }
        }.start();
    }

    private boolean isUpdating() {
        return mIsUpdating;
    }

    /*
     * show/hide progress bar which appears at the bottom of the activity when loading more orders
     */
    private void showLoadingProgress(boolean showProgress) {
        if (isAdded() && mProgress != null) {
            if (showProgress) {
                mProgress.bringToFront();
                mProgress.setVisibility(View.VISIBLE);
            } else {
                mProgress.setVisibility(View.GONE);
            }
        }
    }


    private void setIsUpdating(boolean isUpdating, UpdateAction updateAction) {
        if (!isAdded() || mIsUpdating == isUpdating) {
            return;
        }

        if (updateAction == UpdateAction.REQUEST_OLDER) {
            // show/hide progress bar at bottom if these are older orders
            showLoadingProgress(isUpdating);
        } else if (isUpdating && isOrderAdapterEmpty()) {
            // show swipe-to-refresh if update started and no orders are showing
            mRecyclerView.setRefreshing(true);
        } else if (!isUpdating) {
            // hide swipe-to-refresh progress if update is complete
            mRecyclerView.setRefreshing(false);
        }
        mIsUpdating = isUpdating;

        // if swipe-to-refresh isn't active, keep it disabled during an update - this prevents
        // doing a refresh while another update is already in progress
        if (mRecyclerView != null && !mRecyclerView.isRefreshing()) {
            mRecyclerView.setSwipeToRefreshEnabled(!isUpdating);
        }
    }

    /*
     * bar that appears at the top when new orders have been retrieved
     */
    private boolean isNewOrdersBarShowing() {
        return (mNewOrdersBar != null && mNewOrdersBar.getVisibility() == View.VISIBLE);
    }

    /*
     * scroll listener assigned to the recycler when the "new orders" bar is shown to hide
     * it upon scrolling
     */
    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            hideNewOrdersBar();
        }
    };

    private void showNewOrdersBar() {
        if (!isAdded() || isNewOrdersBarShowing()) {
            return;
        }

        AniUtils.startAnimation(mNewOrdersBar, R.anim.nemur_top_bar_in);
        mNewOrdersBar.setVisibility(View.VISIBLE);

        // assign the scroll listener to hide the bar when the recycler is scrolled, but don't assign
        // it right away since the user may be scrolling when the bar appears (which would cause it
        // to disappear as soon as it's displayed)
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded() && isNewOrdersBarShowing()) {
                    mRecyclerView.addOnScrollListener(mOnScrollListener);
                }
            }
        }, 1000L);

        // remove the gap marker if it's showing, since it's no longer valid
        getOrderAdapter().removeGapMarker();
    }

    private void hideNewOrdersBar() {
        if (!isAdded() || !isNewOrdersBarShowing() || mIsAnimatingOutNewOrdersBar) {
            return;
        }

        mIsAnimatingOutNewOrdersBar = true;

        // remove the onScrollListener assigned in showNewOrdersBar()
        mRecyclerView.removeOnScrollListener(mOnScrollListener);

        Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isAdded()) {
                    mNewOrdersBar.setVisibility(View.GONE);
                    mIsAnimatingOutNewOrdersBar = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
        AniUtils.startAnimation(mNewOrdersBar, R.anim.nemur_top_bar_out, listener);
    }

    /*
     * are we showing all orders with a default tag?
     */
    private NemurOrderListType getOrderListType() {
        return (mOrderListType != null ? mOrderListType : NemurTypes.DEFAULT_ORDER_LIST_TYPE);
    }

    /*
     * called from adapter when user taps a order
     */
    @Override
    public void onOrderSelected(NemurOrder order) {
        if (!isAdded() || order == null) {
            return;
        }

        NemurOrderListType type = getOrderListType();

        switch (type) {
            case TAG_DEFAULT:
                NemurActivityLauncher.showNemurOrderPagerForTag(
                        getActivity(),
                        getCurrentTag(),
                        getOrderListType(),
                        order.buyerId,
                        order.orderId);
                break;
        }
    }

    /*
     * called when user selects a tag from the tag toolbar
     */
    private void onTagChanged(NemurTag tag) {
        if (!isAdded() || isCurrentTag(tag)) {
            return;
        }

        AppLog.d(T.NEMUR, String.format("nemur order list > tag %s displayed", tag.getTagNameForLog()));
        setCurrentTag(tag);
    }

    /*
     * purge reader db if it hasn't been done yet
     */
    private void purgeDatabaseIfNeeded() {
        if (!mHasPurgedNemurDb) {
            AppLog.d(T.NEMUR, "nemur order list > purging database");
            mHasPurgedNemurDb = true;
            NemurDatabase.purgeAsync();
        }
    }

    /*
     * start background service to get the latest default tags if it's time to do so
     */
    private void updateDefaultTagsIfNeeded() {
        if (mLastAutoUpdateDt != null) {
            int minutesSinceLastUpdate = DateTimeUtils.minutesBetween(mLastAutoUpdateDt, new Date());
            if (minutesSinceLastUpdate < 120) {
                return;
            }
        }

        AppLog.d(T.NEMUR, "nemur order list > updating tags");
        mLastAutoUpdateDt = new Date();
        NemurUpdateServiceStarter.startService(getActivity(), EnumSet.of(UpdateTask.TAGS));
    }

    @Override
    public void onScrollToTop() {
        if (isAdded() && getCurrentPosition() > 0) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    // reset the timestamp that determines when default tags/buyers are updated so they're
    // updated when the fragment is recreated (necessary after signin/disconnect)
    public static void resetLastUpdateDate() {
        mLastAutoUpdateDt = null;
    }

    private class LoadTagsTask extends AsyncTask<Void, Void, NemurTagList> {
        private final FilteredRecyclerView.FilterCriteriaAsyncLoaderListener mFilterCriteriaLoaderListener;

        LoadTagsTask(FilteredRecyclerView.FilterCriteriaAsyncLoaderListener listener) {
            mFilterCriteriaLoaderListener = listener;
        }

        @Override
        protected NemurTagList doInBackground(Void... voids) {
            NemurTagList tagList = NemurTagTable.getDefaultTags();

            return tagList;
        }

        @Override
        protected void onPostExecute(NemurTagList tagList) {
            if (mFilterCriteriaLoaderListener != null) {
                //noinspection unchecked
                mFilterCriteriaLoaderListener.onFilterCriteriasLoaded((List) tagList);
            }
        }
    }
}
