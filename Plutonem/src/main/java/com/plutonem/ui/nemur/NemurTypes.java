package com.plutonem.ui.nemur;

public class NemurTypes {
    public static final NemurOrderListType DEFAULT_ORDER_LIST_TYPE = NemurOrderListType.TAG_DEFAULT;

    public enum NemurOrderListType {
        TAG_DEFAULT; // list orders in a default tag

        public boolean isTagType() {
            return this.equals(TAG_DEFAULT);
        }
    }
}
