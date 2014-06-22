package BussinessLogic.ParsingPhoto.Carousel;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import BussinessLogic.SqlLiteHelper.SqlImplementation;

import static java.lang.Math.abs;
import static java.lang.Math.sin;
import static java.lang.StrictMath.cos;

public class Carousel extends CarouselSpinner implements GestureDetector.OnGestureListener {


    private static final String TAG = Carousel.class.getSimpleName();

    private static final boolean localLOGV = false;


    private static final int MIN_QUANTITY = 3;

    private static final int MAX_QUANTITY = 12;

    private static final float MAX_THETA = 15.0f;

    private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;

    private AdapterView.AdapterContextMenuInfo mContextMenuInfo;


    private int mAnimationDuration = 900;


    private Camera mCamera = new Camera();


    private Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
        public void run() {
            mSuppressSelectionChanged = false;
            selectionChanged();
        }
    };


    private int mDownTouchPosition;


    private View mDownTouchView;

    private FlingRotateRunnable mFlingRunnable = new FlingRotateRunnable();


    private GestureDetector mGestureDetector;


    private int mGravity;


    private boolean mIsFirstScroll;

    private int mMaxQuantity = MAX_QUANTITY;


    private int mMinQuantity = MIN_QUANTITY;


    private SqlImplementation _sqlImplementation;

    private boolean mReceivedInvokeKeyDown;

    private View mSelectedChild;


    /**
     * Whether to continuously callback on the item selected listener during a
     * fling.
     */
    private boolean mShouldCallbackDuringFling = true;

    /**
     * Whether to callback when an item that is not selected is clicked.
     */
    private boolean mShouldCallbackOnUnselectedItemClick = true;

    /**
     * When fling runnable runs, it resets this to false. Any method along the
     * path until the end of its run() can set this to true to abort any
     * remaining fling. For example, if we've reached either the leftmost or
     * rightmost item, we will set this to true.
     */
    private boolean mShouldStopFling;

    /**
     * If true, do not callback to item selected listener.
     */
    private boolean mSuppressSelectionChanged;

    /**
     * The axe angle
     */
    private float mTheta = (float)(15.0f*(Math.PI/360.0));

    /**
     * If items should be reflected
     */
    private boolean mUseReflection;

    // Constructors

    public Carousel(Context context)  {
        this(context, null);
        _sqlImplementation = new SqlImplementation(context);

    }

    public Carousel(Context context, AttributeSet attrs)  {
        this(context, attrs, 0);
        _sqlImplementation = new SqlImplementation(context);
    }

    public Carousel(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        _sqlImplementation = new SqlImplementation(context);

        // It's needed to make items with greater value of
        // z coordinate to be behind items with lesser z-coordinate
        setChildrenDrawingOrderEnabled(true);

        // Making user gestures available
        mGestureDetector = new GestureDetector(this.getContext(), this);
        mGestureDetector.setIsLongpressEnabled(true);

        // It's needed to apply 3D transforms to items
        // before they are drawn
        setStaticTransformationsEnabled(true);

        // Retrieve settings
        ;
        mAnimationDuration =400;
        mUseReflection =  false;
        int selectedItem = 0;


        ArrayList<Integer> keys = new ArrayList<Integer>(getElementsFromDatabase().keySet());
        ArrayList<String> images = new ArrayList<String>(getElementsFromDatabase().values());

        Collections.sort(images);
        Collections.sort(keys);

        int min =  MIN_QUANTITY;
        int max =  MAX_QUANTITY;

        float mTheta =  MAX_THETA;


        mMinQuantity =  MIN_QUANTITY;
        mMaxQuantity =  MAX_QUANTITY;

        // Initialize image adapter
        ImageAdapter adapter = new ImageAdapter(getContext());

        adapter.SetImages(images, keys);

        setAdapter(adapter);

        if(selectedItem < 0 || selectedItem >= adapter.getCount())
            selectedItem = 0;

        // next time we go through layout with this value
        setNextSelectedPositionInt(selectedItem);

    }

    // View overrides

    // These are for use with horizontal scrollbar

    /**
     * Compute the horizontal extent of the horizontal scrollbar's thumb
     * within the horizontal range. This value is used to compute
     * the length of the thumb within the scrollbar's track.
     */
    @Override
    protected int computeHorizontalScrollExtent() {
        // Only 1 item is considered to be selected
        return 1;
    }



    /**
     * Compute the horizontal offset of the horizontal scrollbar's
     * thumb within the horizontal range. This value is used to compute
     * the position of the thumb within the scrollbar's track.
     */
    @Override
    protected int computeHorizontalScrollOffset() {
        // Current scroll position is the same as the selected position
        return mSelectedPosition;
    }

    /**
     * Compute the horizontal range that the horizontal scrollbar represents.
     */
    @Override
    protected int computeHorizontalScrollRange() {
        // Scroll range is the same as the item count
        return mItemCount;
    }

    /**
     * Implemented to handle touch screen motion events.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Give everything to the gesture detector
        boolean retValue = mGestureDetector.onTouchEvent(event);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            // Helper method for lifted finger
            onUp();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            onCancel();
        }

        return retValue;
    }

    /**
     * Extra information about the item for which the context menu should be shown.
     */
    @Override
    protected ContextMenuInfo getContextMenuInfo() {
        return mContextMenuInfo;
    }

    /**
     * Bring up the context menu for this view.
     */
    @Override
    public boolean showContextMenu() {

        if (isPressed() && mSelectedPosition >= 0) {
            int index = mSelectedPosition - mFirstPosition;
            View v = getChildAt(index);
            return dispatchLongPress(v, mSelectedPosition, mSelectedRowId);
        }

        return false;
    }

    /**
     * Handles left, right, and clicking
     * @see android.view.View#onKeyDown
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_UP:
                ////if (movePrevious()) {
                playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
                ////}
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                /////if (moveNext()) {
                playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN);
                ////}
                return true;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                mReceivedInvokeKeyDown = true;
                // fallthrough to default handling
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {

                if (mReceivedInvokeKeyDown) {
                    if (mItemCount > 0) {

                        dispatchPress(mSelectedChild);
                        postDelayed(new Runnable() {
                            public void run() {
                                dispatchUnpress();
                            }
                        }, ViewConfiguration.getPressedStateDuration());

                        int selectedIndex = mSelectedPosition - mFirstPosition;
                        performItemClick(getChildAt(selectedIndex), mSelectedPosition, mAdapter
                                .getItemId(mSelectedPosition));
                    }
                }

                // Clear the flag
                mReceivedInvokeKeyDown = false;

                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        /*
         * The gallery shows focus by focusing the selected item. So, give
         * focus to our selected item instead. We steal keys from our
         * selected item elsewhere.
         */
        if (gainFocus && mSelectedChild != null) {
            mSelectedChild.requestFocus(direction);
        }

    }

    // ViewGroup overrides

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public void dispatchSetSelected(boolean selected) {
        /*
         * We don't want to pass the selected state given from its parent to its
         * children since this widget itself has a selected state to give to its
         * children.
         */
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {

        // Show the pressed state on the selected child
        if (mSelectedChild != null) {
            mSelectedChild.setPressed(pressed);
        }
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {

        final int longPressPosition = getPositionForView(originalView);
        if (longPressPosition < 0) {
            return false;
        }

        final long longPressId = mAdapter.getItemId(longPressPosition);
        return dispatchLongPress(originalView, longPressPosition, longPressId);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Gallery steals all key events
        return event.dispatch(this, null, null);
    }

    /**
     * Index of the child to draw for this iteration
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {

        // Sort Carousel items by z coordinate in reverse order
        ArrayList<CarouselItem> sl = new ArrayList<CarouselItem>();
        for(int j = 0; j < childCount; j++)
        {
            CarouselItem view = (CarouselItem)getAdapter().getView(j,null, null);
            if(i == 0)
                view.setDrawn(false);
            sl.add((CarouselItem)getAdapter().getView(j,null, null));
        }

        Collections.sort(sl);

        // Get first undrawn item in array and get result index
        int idx = 0;

        for(CarouselItem civ : sl)
        {
            if(!civ.isDrawn())
            {
                civ.setDrawn(true);
                idx = civ.getIndex();
                break;
            }
        }

        return idx;

    }

    /**
     * Transform an item depending on it's coordinates
     */
    @Override
    protected boolean getChildStaticTransformation(View child, Transformation transformation) {

        transformation.clear();
        transformation.setTransformationType(Transformation.TYPE_MATRIX);

        // Center of the view
        float centerX = (float)getWidth()/2, centerY = (float)getHeight()/2;

        // Save camera
        mCamera.save();

        // Translate the item to it's coordinates
        final Matrix matrix = transformation.getMatrix();

        mCamera.translate(((CarouselItem)child).getItemX(), ((CarouselItem)child).getItemY(),
                ((CarouselItem)child).getItemZ());

        // Align the item
        mCamera.getMatrix(matrix);

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

        float[] values = new float[9];
        matrix.getValues(values);

        // Restore camera
        mCamera.restore();

        Matrix mm = new Matrix();
        mm.setValues(values);
        ((CarouselItem)child).setCIMatrix(mm);

        //http://code.google.com/p/android/issues/detail?id=35178
        child.invalidate();
        return true;
    }

    // CarouselAdapter overrides

    /**
     * Setting up images
     */
    void layout(int delta, boolean animate){

        if (mDataChanged) {
            handleDataChanged();
        }

        // Handle an empty gallery by removing all views.
        if (getCount() == 0) {
            resetList();
            return;
        }

        // Update to the new selected position.
        if (mNextSelectedPosition >= 0) {
            setSelectedPositionInt(mNextSelectedPosition);
        }

        // All views go in recycler while we are in layout
        recycleAllViews();

        // Clear out old views
        detachAllViewsFromParent();


        int count = getAdapter().getCount();
        float angleUnit = 360.0f / count;

        float angleOffset = mSelectedPosition * angleUnit;
        for(int i = 0; i< getAdapter().getCount(); i++){
            float angle = angleUnit * i - angleOffset;
            if(angle < 0.0f)
                angle = 360.0f + angle;
            makeAndAddView(i, angle);
        }

        // Flush any cached views that did not get reused above
        mRecycler.clear();

        invalidate();

        setNextSelectedPositionInt(mSelectedPosition);

        checkSelectionChanged();

        ////////mDataChanged = false;
        mNeedSync = false;

        updateSelectedItemMetadata();

    }

    /**
     * Setting up images after layout changed
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        /*
         * Remember that we are in layout to prevent more layout request from
         * being generated.
         */
        mInLayout = true;
        layout(0, false);
        mInLayout = false;
    }

    @Override
    void selectionChanged() {
        if (!mSuppressSelectionChanged) {
            super.selectionChanged();
        }
    }

    @Override
    void setSelectedPositionInt(int position) {
        super.setSelectedPositionInt(position);
        super.setNextSelectedPositionInt(position);

        // Updates any metadata we keep about the selected item.
        updateSelectedItemMetadata();
    }


    // Rotation class for the Carousel

    private class FlingRotateRunnable implements Runnable {

        /**
         * Tracks the decay of a fling rotation
         */
        private Rotator mRotator;

        /**
         * Angle value reported by mRotator on the previous fling
         */
        private float mLastFlingAngle;

        /**
         * Constructor
         */
        public FlingRotateRunnable(){
            mRotator = new Rotator(getContext());
        }

        private void startCommon() {
            // Remove any pending flings
            removeCallbacks(this);
        }

        public void startUsingVelocity(float initialVelocity) {
            if (initialVelocity == 0) return;

            startCommon();

            mLastFlingAngle = 0.0f;

            mRotator.fling(initialVelocity);

            post(this);
        }


        public void startUsingDistance(float deltaAngle) {
            if (deltaAngle == 0) return;

            startCommon();

            mLastFlingAngle = 0;
            synchronized(this)
            {
                mRotator.startRotate(0.0f, -deltaAngle, mAnimationDuration);
            }
            post(this);
        }

        public void stop(boolean scrollIntoSlots) {
            removeCallbacks(this);
            endFling(scrollIntoSlots);
        }

        private void endFling(boolean scrollIntoSlots) {
            /*
             * Force the scroller's status to finished (without setting its
             * position to the end)
             */
            synchronized(this){
                mRotator.forceFinished(true);
            }

            if (scrollIntoSlots) scrollIntoSlots();
        }

        public void run() {
            if (Carousel.this.getChildCount() == 0) {
                endFling(true);
                return;
            }

            mShouldStopFling = false;

            final Rotator rotator;
            final float angle;
            boolean more;
            synchronized(this){
                rotator = mRotator;
                more = rotator.computeAngleOffset();
                angle = rotator.getCurrAngle();
            }


            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            float delta = mLastFlingAngle - angle;

            //////// Shoud be reworked
            trackMotionScroll(delta);

            if (more && !mShouldStopFling) {
                mLastFlingAngle = angle;
                post(this);
            } else {
                mLastFlingAngle = 0.0f;
                endFling(true);
            }

        }

    }

    // Image adapter class for the Carousel

    private class ImageAdapter extends BaseAdapter {

        private Context mContext;
        private CarouselItem[] mImages;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public void SetImages(ArrayList<String> array, ArrayList<Integer> keys){

            final int reflectionGap = 4;

            mImages = new CarouselItem[array.size()];

            for(int i = 0; i< array.size(); i++)
            {
                //
                Bitmap originalImage = BitmapFactory.decodeFile(array.get(i));


                int width = originalImage.getWidth();
                int height = originalImage.getHeight();

                // This will not scale but will flip on the Y axis
                Matrix matrix = new Matrix();
                matrix.preScale(1, -1);

                // Create a Bitmap with the flip matrix applied to it.
                // We only want the bottom half of the image
                Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                        height / 2, width, height / 2, matrix, false);

                // Create a new bitmap with same width but taller to fit
                // reflection
                Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                        (height + height / 2), Config.ARGB_8888);

                // Create a new Canvas with the bitmap that's big enough for
                // the image plus gap plus reflection
                Canvas canvas = new Canvas(bitmapWithReflection);
                // Draw in the original image
                canvas.drawBitmap(originalImage, 0, 0, null);
                // Draw in the gap
                Paint defaultPaint = new Paint();
                canvas.drawRect(0, height, width, height + reflectionGap,
                        defaultPaint);
                // Draw in the reflection
                canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
                        null);

                // Create a shader that is a linear gradient that covers the
                // reflection
                Paint paint = new Paint();
                LinearGradient shader = new LinearGradient(0,
                        originalImage.getHeight(), 0,
                        bitmapWithReflection.getHeight() + reflectionGap,
                        0x70ffffff, 0x00ffffff, TileMode.CLAMP);
                // Set the paint to use this shader (linear gradient)
                paint.setShader(shader);
                // Set the Transfer mode to be porter duff and destination in
                paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
                // Draw a rectangle using the paint with our linear gradient
                canvas.drawRect(0, height, width,
                        bitmapWithReflection.getHeight() + reflectionGap, paint);

                originalImage = bitmapWithReflection;


                CarouselItem item = new CarouselItem(mContext);
                item.setIndex(i);
                item.setKey(keys.get(array.size() - i-1));
                item.setImageBitmap(originalImage);

                mImages[i] = item;


            }


        }

        public int getCount() {
            if(mImages == null)
                return 0;
            else
                return mImages.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public int getKey(int position)
        {
            return mImages[position].getKey();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return mImages[position];
        }

    }

    // OnGestureListener implementation

    public boolean onDown(MotionEvent e) {
        // Kill any existing fling/scroll
        mFlingRunnable.stop(false);

        ///// Don't know yet what for it is
        // Get the item's view that was touched
        mDownTouchPosition = pointToPosition((int) e.getX(), (int) e.getY());

        if (mDownTouchPosition >= 0) {
            mDownTouchView = getChildAt(mDownTouchPosition - mFirstPosition);
            mDownTouchView.setPressed(true);
        }

        // Reset the multiple-scroll tracking state
        mIsFirstScroll = true;

        // Must return true to get matching events for this down event.
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (!mShouldCallbackDuringFling) {
            // We want to suppress selection changes

            // Remove any future code to set mSuppressSelectionChanged = false
            removeCallbacks(mDisableSuppressSelectionChangedRunnable);

            // This will get reset once we scroll into slots
            if (!mSuppressSelectionChanged) mSuppressSelectionChanged = true;
        }

        // Fling the gallery!

        //mFlingRunnable.startUsingVelocity((int) -velocityX);
        mFlingRunnable.startUsingVelocity((int) velocityY);

        return true;
    }


    public void onLongPress(MotionEvent e) {

        if (mDownTouchPosition < 0) {
            return;
        }


        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        long id = getItemIdAtPosition(mDownTouchPosition);
        dispatchLongPress(mDownTouchView, mDownTouchPosition, id);

    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {

        if (localLOGV) Log.v(TAG, String.valueOf(e2.getX() - e1.getX()));

        /*
         * Now's a good time to tell our parent to stop intercepting our events!
         * The user has moved more than the slop amount, since GestureDetector
         * ensures this before calling this method. Also, if a parent is more
         * interested in this touch's events than we are, it would have
         * intercepted them by now (for example, we can assume when a Gallery is
         * in the ListView, a vertical scroll would not end up in this method
         * since a ListView would have intercepted it by now).
         */


        getParent().requestDisallowInterceptTouchEvent(true);

        // As the user scrolls, we want to callback selection changes so related-
        // info on the screen is up-to-date with the gallery's selection
        if (!mShouldCallbackDuringFling) {
            if (mIsFirstScroll) {
                /*
                 * We're not notifying the client of selection changes during
                 * the fling, and this scroll could possibly be a fling. Don't
                 * do selection changes until we're sure it is not a fling.
                 */
                if (!mSuppressSelectionChanged) mSuppressSelectionChanged = true;
                postDelayed(mDisableSuppressSelectionChangedRunnable, SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT);
            }
        } else {
            if (mSuppressSelectionChanged) mSuppressSelectionChanged = false;
        }

        // Track the motion
        trackMotionScroll(/* -1 * */ (int) distanceY);

        mIsFirstScroll = false;
        return true;
    }


    public boolean onSingleTapUp(MotionEvent e) {
        if (mDownTouchPosition >= 0) {

            // Pass the click so the client knows, if it wants to.
            if (mShouldCallbackOnUnselectedItemClick || mDownTouchPosition == mSelectedPosition) {
                performItemClick(mDownTouchView, mSelectedPosition, mAdapter
                        .getItemId(mSelectedPosition));
            }

            return true;
        }

        return false;
    }


    ///// Unused gestures
    public void onShowPress(MotionEvent e) {
    }


    private void Calculate3DPosition(CarouselItem child, int diameter, float angleOffset){


        //depends speed of carousel
        angleOffset = angleOffset * (float)(Math.PI/180.0f);

        float x = (float)(-diameter/2  * cos(angleOffset)*0.00001);
        float z = diameter/2.0f * (1.0f - (float)cos(angleOffset));
        float y = (float) (diameter/2*sin(angleOffset)) + diameter/2 - getWidth()*0.85f;

        child.setItemX(x);
        child.setItemZ(z);
        child.setItemY(y);

    }



    /**
     * Figure out vertical placement based on mGravity
     *
     * @param child Child to place
     * @return Where the top of the child should be
     */
    private int calculateTop(View child, boolean duringLayout) {
        int myHeight = duringLayout ? getMeasuredHeight() : getHeight();
        int childHeight = duringLayout ? child.getMeasuredHeight() : child.getHeight();

        int childTop = 0;

        switch (mGravity) {
            case Gravity.TOP:
                childTop = mSpinnerPadding.top;
                break;
            case Gravity.CENTER_VERTICAL:
                int availableSpace = myHeight - mSpinnerPadding.bottom
                        - mSpinnerPadding.top - childHeight;
                childTop = mSpinnerPadding.top + (availableSpace / 2);
                break;
            case Gravity.BOTTOM:
                childTop = myHeight - mSpinnerPadding.bottom - childHeight;
                break;
        }
        return childTop;
    }

    private boolean dispatchLongPress(View view, int position, long id) {
        boolean handled = false;

        if (mOnItemLongClickListener != null) {
            handled = mOnItemLongClickListener.onItemLongClick(this, mDownTouchView,
                    mDownTouchPosition, id);
        }

        if (!handled) {
            mContextMenuInfo = new AdapterView.AdapterContextMenuInfo(view, position, id);
            handled = super.showContextMenuForChild(this);
        }

        if (handled) {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }

        return handled;
    }

    private void dispatchPress(View child) {

        if (child != null) {
            child.setPressed(true);
        }

        setPressed(true);
    }

    private void dispatchUnpress() {

        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).setPressed(false);
        }

        setPressed(false);
    }

    /**
     * @return The center of this Gallery.
     */
    private int getCenterOfGallery() {
        return (getWidth() - Carousel.this.getPaddingLeft() - Carousel.this.getPaddingRight()) / 2 +
                Carousel.this.getPaddingLeft();
    }

    /**
     * @return The center of the given view.
     */
    private static int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }



    float getLimitedMotionScrollAmount(boolean motionToLeft, float deltaY) {
        int extremeItemPosition = motionToLeft ? Carousel.this.getCount() - 1 : 0;
        View extremeChild = getChildAt(extremeItemPosition - Carousel.this.getFirstVisiblePosition());

        if (extremeChild == null) {
            return deltaY;
        }

        int extremeChildCenter = getCenterOfView(extremeChild);
        int galleryCenter = getCenterOfGallery();

        if (motionToLeft) {
            if (extremeChildCenter <= galleryCenter) {

                // The extreme child is past his boundary point!
                return 0;
            }
        } else {
            if (extremeChildCenter >= galleryCenter) {

                // The extreme child is past his boundary point!
                return 0;
            }
        }
        int centerDifference = galleryCenter - extremeChildCenter;

        return motionToLeft
                ? Math.max(centerDifference, deltaY)
                : Math.min(centerDifference, deltaY);
    }


    int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX) {
        int extremeItemPosition = motionToLeft ? mItemCount - 1 : 0;
        View extremeChild = getChildAt(extremeItemPosition - mFirstPosition);

        if (extremeChild == null) {
            return deltaX;
        }

        int extremeChildCenter = getCenterOfView(extremeChild);
        int galleryCenter = getCenterOfGallery();

        if (motionToLeft) {
            if (extremeChildCenter <= galleryCenter) {

                // The extreme child is past his boundary point!
                return 0;
            }
        } else {
            if (extremeChildCenter >= galleryCenter) {

                // The extreme child is past his boundary point!
                return 0;
            }
        }

        int centerDifference = galleryCenter - extremeChildCenter;

        return motionToLeft
                ? Math.max(centerDifference, deltaX)
                : Math.min(centerDifference, deltaX);
    }

    private void makeAndAddView(int position, float angleOffset) {
        CarouselItem child;

           // Nothing found in the recycler -- ask the adapter for a view
        child = (CarouselItem)mAdapter.getView(position, null, this);

        // Position the view
        setUpChild(child, child.getIndex(), angleOffset);

    }

    void onCancel(){
        onUp();
    }

    /**
     * Called when rotation is finished
     */
    private void onFinishedMovement() {
        if (mSuppressSelectionChanged) {
            mSuppressSelectionChanged = false;

            // We haven't been callbacking during the fling, so do it now
            super.selectionChanged();
        }
        checkSelectionChanged();
        invalidate();

    }

    void onUp(){
        if (mFlingRunnable.mRotator.isFinished()) {
            scrollIntoSlots();
        }
        dispatchUnpress();
    }

    /**
     * Brings an item with nearest to 0 degrees angle to this angle and sets it selected
     */
    private void scrollIntoSlots(){

        // Nothing to do
        if (getChildCount() == 0 || mSelectedChild == null) return;

        // get nearest item to the 0 degrees angle
        // Sort itmes and get nearest angle
        float angle;
        int position;

        ArrayList<CarouselItem> arr = new ArrayList<CarouselItem>();

        for(int i = 0; i < getAdapter().getCount(); i++)
            arr.add(((CarouselItem)getAdapter().getView(i, null, null)));

        Collections.sort(arr, new Comparator<CarouselItem>(){

            public int compare(CarouselItem c1, CarouselItem c2) {
                int a1 = (int)c1.getCurrentAngle();
                if(a1 > 180)
                    a1 = 360 - a1;
                int a2 = (int)c2.getCurrentAngle();
                if(a2 > 180)
                    a2 = 360 - a2;
                return (a1 - a2) ;
            }

        });


        angle = arr.get(0).getCurrentAngle();

        // Make it minimum to rotate
        if(angle > 180.0f)
            angle = -(360.0f - angle);

        // Start rotation if needed
        if(abs(angle) > 4 )
        {
            mFlingRunnable.startUsingDistance(-angle);
        }
        else
        {
            // Set selected position
            position = arr.get(0).getIndex();
            setSelectedPositionInt(position);
            onFinishedMovement();
        }


    }

    void scrollToChild(int i){

        CarouselItem view = (CarouselItem)getAdapter().getView(i, null, null);
        float angle = view.getCurrentAngle();

        if(angle == 0)
            angle = 360.0f - angle;
        else
            angle = -angle;

        if(angle > 180.0f)
            return;

        mFlingRunnable.startUsingDistance(angle);


    }

    /**
     * Whether or not to callback on any {@link #getOnItemSelectedListener()}
     * while the items are being flinged. If false, only the final selected item
     * will cause the callback. If true, all items between the first and the
     * final will cause callbacks.
     *
     * @param shouldCallback Whether or not to callback on the listener while
     *            the items are being flinged.
     */
    public void setCallbackDuringFling(boolean shouldCallback) {
        mShouldCallbackDuringFling = shouldCallback;
    }

    /**
     * Whether or not to callback when an item that is not selected is clicked.
     * If false, the item will become selected (and re-centered). If true, the
     * {@link #getOnItemClickListener()} will get the callback.
     *
     * @param shouldCallback Whether or not to callback on the listener when a
     *            item that is not selected is clicked.
     * @hide
     */
    public void setCallbackOnUnselectedItemClick(boolean shouldCallback) {
        mShouldCallbackOnUnselectedItemClick = shouldCallback;
    }

    /**
     * Sets how long the transition animation should run when a child view
     * changes position. Only relevant if animation is turned on.
     *
     * @param animationDurationMillis The duration of the transition, in
     *        milliseconds.
     *
     * @attr ref android.R.styleable#Gallery_animationDuration
     */
    public void setAnimationDuration(int animationDurationMillis) {
        mAnimationDuration = animationDurationMillis;
    }

    public void setGravity(int gravity){
        if (mGravity != gravity) {
            mGravity = gravity;
            requestLayout();
        }
    }



    private void setUpChild(CarouselItem child, int index, float angleOffset) {

        // Ignore any layout parameters for child, use wrap content
        addViewInLayout(child, -1 /*index*/, generateDefaultLayoutParams());

        child.setSelected(index == mSelectedPosition);

        int h;
        int w;
        int d;


        h = child.getMeasuredHeight();
        w = getMeasuredWidth() - getPaddingLeft() - getPaddingRight()/3;


        child.setCurrentAngle(angleOffset);

        // Measure child
        child.measure(w, h);

        int childLeft;

        // Position vertically based on gravity setting
        int childTop = calculateTop(child, true);

        childLeft = 0;

        child.layout(childLeft, childTop, w, h);

        Calculate3DPosition(child, w*(getAdapter().getCount()/4), angleOffset);


    }

    /**
     * Tracks a motion scroll. In reality, this is used to do just about any
     * movement to items (touch scroll, arrow-key scroll, set an item as selected).
     *
     * @param deltaAngle Change in X from the previous event.
     */
    void trackMotionScroll(float deltaAngle) {

        if (getChildCount() == 0) {
            return;
        }

        for(int i = 0; i < getAdapter().getCount(); i++){

            CarouselItem child = (CarouselItem)getAdapter().getView(i, null, null);

            float angle = child.getCurrentAngle();
            angle += deltaAngle;

            while(angle > 360.0f)
                angle -= 360.0f;

            while(angle < 0.0f)
                angle += 360.0f;

            child.setCurrentAngle(angle);
            Calculate3DPosition(child, getWidth(), angle);

        }

        // Clear unused views
        mRecycler.clear();

        invalidate();
    }

    private void updateSelectedItemMetadata() {

        View oldSelectedChild = mSelectedChild;

        View child = mSelectedChild = getChildAt(mSelectedPosition - mFirstPosition);
        if (child == null) {
            return;
        }

        child.setSelected(true);
        child.setFocusable(true);

        if (hasFocus()) {
            child.requestFocus();
        }

        // We unfocus the old child down here so the above hasFocus check
        // returns true
        if (oldSelectedChild != null) {

            // Make sure its drawable state doesn't contain 'selected'
            oldSelectedChild.setSelected(false);

            // Make sure it is not focusable anymore, since otherwise arrow keys
            // can make this one be focused
            oldSelectedChild.setFocusable(false);
        }

    }


    private Map<Integer, String> getElementsFromDatabase()
    {
        return _sqlImplementation.getImagePathes();
    }

}
