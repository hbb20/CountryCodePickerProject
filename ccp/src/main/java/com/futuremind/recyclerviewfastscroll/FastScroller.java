package com.futuremind.recyclerviewfastscroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.futuremind.recyclerviewfastscroll.viewprovider.DefaultScrollerViewProvider;
import com.futuremind.recyclerviewfastscroll.viewprovider.ScrollerViewProvider;
import com.hbb20.R;

/**
 * Created by mklimczak on 28/07/15.
 */
public class FastScroller extends LinearLayout {

    private static final int STYLE_NONE = -1;
    private final RecyclerViewScrollListener scrollListener = new RecyclerViewScrollListener(this);
    private RecyclerView recyclerView;

    private View bubble;
    private View handle;
    private TextView bubbleTextView;

    private int bubbleOffset;
    private int handleColor;
    private int bubbleColor;
    private int bubbleTextAppearance;
    private int scrollerOrientation;

    //TODO the name should be fixed, also check if there is a better way of handling the visibility, because this is somewhat convoluted
    private int maxVisibility;

    private boolean manuallyChangingPosition;

    private ScrollerViewProvider viewProvider;
    private SectionTitleProvider titleProvider;

    public FastScroller(Context context) {
        this(context, null);
    }

    public FastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastScroller(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setClipChildren(false);
        TypedArray style = context.obtainStyledAttributes(attrs, R.styleable.fastscroll__fastScroller, R.attr.fastscroll__style, 0);
        try {
            bubbleColor = style.getColor(R.styleable.fastscroll__fastScroller_fastscroll__bubbleColor, STYLE_NONE);
            handleColor = style.getColor(R.styleable.fastscroll__fastScroller_fastscroll__handleColor, STYLE_NONE);
            bubbleTextAppearance = style.getResourceId(R.styleable.fastscroll__fastScroller_fastscroll__bubbleTextAppearance, STYLE_NONE);
        } finally {
            style.recycle();
        }
        maxVisibility = getVisibility();
        setViewProvider(new DefaultScrollerViewProvider());
    }

    /**
     * Attach the {@link FastScroller} to {@link RecyclerView}. Should be used after the adapter is set
     * to the {@link RecyclerView}. If the adapter implements SectionTitleProvider, the FastScroller
     * will show a bubble with title.
     *
     * @param recyclerView A {@link RecyclerView} to attach the {@link FastScroller} to.
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        if (recyclerView.getAdapter() instanceof SectionTitleProvider)
            titleProvider = (SectionTitleProvider) recyclerView.getAdapter();
        recyclerView.addOnScrollListener(scrollListener);
        invalidateVisibility();
        recyclerView.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                invalidateVisibility();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                invalidateVisibility();
            }
        });
    }

    /**
     * Set the orientation of the {@link FastScroller}. The orientation of the {@link FastScroller}
     * should generally match the orientation of connected  {@link RecyclerView} for good UX but it's not enforced.
     * Note: This method is overridden from {@link LinearLayout#setOrientation(int)} but for {@link FastScroller}
     * it has a totally different meaning.
     *
     * @param orientation of the {@link FastScroller}. {@link #VERTICAL} or {@link #HORIZONTAL}
     */
    @Override
    public void setOrientation(int orientation) {
        scrollerOrientation = orientation;
        //switching orientation, because orientation in linear layout
        //is something different than orientation of fast scroller
        super.setOrientation(orientation == HORIZONTAL ? VERTICAL : HORIZONTAL);
    }

    /**
     * Set the background color of the bubble.
     *
     * @param color Color in hex notation with alpha channel, e.g. 0xFFFFFFFF
     */
    public void setBubbleColor(int color) {
        bubbleColor = color;
        invalidate();
    }

    /**
     * Set the background color of the handle.
     *
     * @param color Color in hex notation with alpha channel, e.g. 0xFFFFFFFF
     */
    public void setHandleColor(int color) {
        handleColor = color;
        invalidate();
    }

    /**
     * Sets the text appearance of the bubble.
     *
     * @param textAppearanceResourceId The id of the resource to be used as text appearance of the bubble.
     */
    public void setBubbleTextAppearance(int textAppearanceResourceId) {
        bubbleTextAppearance = textAppearanceResourceId;
        invalidate();
    }

    /**
     * Add a {@link com.futuremind.recyclerviewfastscroll.RecyclerViewScrollListener.ScrollerListener}
     * to be notified of user scrolling
     *
     * @param listener
     */
    public void addScrollerListener(RecyclerViewScrollListener.ScrollerListener listener) {
        scrollListener.addScrollerListener(listener);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        initHandleMovement();
        bubbleOffset = viewProvider.getBubbleOffset();

        applyStyling(); //TODO this doesn't belong here, even if it works

        if (!isInEditMode()) {
            //sometimes recycler starts with a defined scroll (e.g. when coming from saved state)
            scrollListener.updateHandlePosition(recyclerView);
        }

    }

    private void applyStyling() {
        if (bubbleColor != STYLE_NONE) setBackgroundTint(bubbleTextView, bubbleColor);
        if (handleColor != STYLE_NONE) setBackgroundTint(handle, handleColor);
        if (bubbleTextAppearance != STYLE_NONE)
            TextViewCompat.setTextAppearance(bubbleTextView, bubbleTextAppearance);
    }

    private void setBackgroundTint(View view, int color) {
        final Drawable background = DrawableCompat.wrap(view.getBackground());
        if (background == null) return;
        DrawableCompat.setTint(background.mutate(), color);
        Utils.setBackground(view, background);
    }

    private void initHandleMovement() {
        handle.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                requestDisallowInterceptTouchEvent(true);
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (titleProvider != null && event.getAction() == MotionEvent.ACTION_DOWN)
                        viewProvider.onHandleGrabbed();
                    manuallyChangingPosition = true;
                    float relativePos = getRelativeTouchPosition(event);
                    setScrollerPosition(relativePos);
                    setRecyclerViewPosition(relativePos);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    manuallyChangingPosition = false;
                    if (titleProvider != null) viewProvider.onHandleReleased();
                    return true;
                }
                return false;
            }
        });
    }

    private float getRelativeTouchPosition(MotionEvent event) {
        if (isVertical()) {
            float yInParent = event.getRawY() - Utils.getViewRawY(handle);
            return yInParent / (getHeight() - handle.getHeight());
        } else {
            float xInParent = event.getRawX() - Utils.getViewRawX(handle);
            return xInParent / (getWidth() - handle.getWidth());
        }
    }

    @Override
    public void setVisibility(int visibility) {
        maxVisibility = visibility;
        invalidateVisibility();
    }

    private void invalidateVisibility() {
        if (
                recyclerView.getAdapter() == null ||
                        recyclerView.getAdapter().getItemCount() == 0 ||
                        recyclerView.getChildAt(0) == null ||
                        isRecyclerViewNotScrollable() ||
                        maxVisibility != View.VISIBLE
        ) {
            super.setVisibility(INVISIBLE);
        } else {
            super.setVisibility(VISIBLE);
        }
    }

    private boolean isRecyclerViewNotScrollable() {
        if (isVertical()) {
            return recyclerView.getChildAt(0).getHeight() * recyclerView.getAdapter().getItemCount() <= recyclerView.getHeight();
        } else {
            return recyclerView.getChildAt(0).getWidth() * recyclerView.getAdapter().getItemCount() <= recyclerView.getWidth();
        }
    }

    private void setRecyclerViewPosition(float relativePos) {
        if (recyclerView == null) return;
        int itemCount = recyclerView.getAdapter().getItemCount();
        int targetPos = (int) Utils.getValueInRange(0, itemCount - 1, (int) (relativePos * (float) itemCount));
        recyclerView.scrollToPosition(targetPos);
        if (titleProvider != null && bubbleTextView != null)
            bubbleTextView.setText(titleProvider.getSectionTitle(targetPos));
    }

    void setScrollerPosition(float relativePos) {
        if (isVertical()) {
            bubble.setY(Utils.getValueInRange(
                    0,
                    getHeight() - bubble.getHeight(),
                    relativePos * (getHeight() - handle.getHeight()) + bubbleOffset)
            );
            handle.setY(Utils.getValueInRange(
                    0,
                    getHeight() - handle.getHeight(),
                    relativePos * (getHeight() - handle.getHeight()))
            );
        } else {
            bubble.setX(Utils.getValueInRange(
                    0,
                    getWidth() - bubble.getWidth(),
                    relativePos * (getWidth() - handle.getWidth()) + bubbleOffset)
            );
            handle.setX(Utils.getValueInRange(
                    0,
                    getWidth() - handle.getWidth(),
                    relativePos * (getWidth() - handle.getWidth()))
            );
        }
    }

    public boolean isVertical() {
        return scrollerOrientation == VERTICAL;
    }

    boolean shouldUpdateHandlePosition() {
        return handle != null && !manuallyChangingPosition && recyclerView.getChildCount() > 0;
    }

    ScrollerViewProvider getViewProvider() {
        return viewProvider;
    }

    /**
     * Enables custom layout for {@link FastScroller}.
     *
     * @param viewProvider A {@link ScrollerViewProvider} for the {@link FastScroller} to use when building layout.
     */
    public void setViewProvider(ScrollerViewProvider viewProvider) {
        removeAllViews();
        this.viewProvider = viewProvider;
        viewProvider.setFastScroller(this);
        bubble = viewProvider.provideBubbleView(this);
        handle = viewProvider.provideHandleView(this);
        bubbleTextView = viewProvider.provideBubbleTextView();
        addView(bubble);
        addView(handle);
    }
}
