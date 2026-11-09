package com.mugishajc.notes.ui;

import androidx.annotation.StringRes;

import com.mugishajc.notes.R;
import com.mugishajc.notes.data.Category;

final class CategoryLabels {

    private CategoryLabels() {
    }

    @StringRes
    static int of(Category category) {
        switch (category) {
            case WORK:
                return R.string.category_work;
            case IDEAS:
                return R.string.category_ideas;
            case TODO:
                return R.string.category_todo;
            case PERSONAL:
                return R.string.category_personal;
            case OTHER:
            default:
                return R.string.category_other;
        }
    }
}
