package com.example.csit321.SupportClass;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecorator extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public SpacingItemDecorator(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets (@NonNull Rect outRech, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        outRech.bottom = verticalSpaceHeight;
    }
}
