# SupportPercentLayoutBug
To demonstrate https://code.google.com/p/android/issues/detail?id=186875

Percent layouts (PercentFrameLayout & PercentRelativeLayout) don't do a good job of
letting you use <include> tags to reuse layouts. Both the issue and a simple fix are
presented in this repo's app.

### ISSUE
#### LayoutInflater#parseInclude

```java
// We try to load the layout params set in the <include /> tag. If
// they don't exist, we will rely on the layout params set in the
// included XML file.
// During a layoutparams generation, a runtime exception is thrown
// if either layout_width or layout_height is missing. We catch
// this exception and set localParams accordingly: true means we
// successfully loaded layout params from the <include /> tag,
// false means we need to rely on the included layout params.
ViewGroup.LayoutParams params = null;
try {
    params = group.generateLayoutParams(attrs);
} catch (RuntimeException e) {
    params = group.generateLayoutParams(childAttrs);
} finally {
    if (params != null) {
        view.setLayoutParams(params);
    }
}
```

It banks on the fact that if *layout_width* or *layout_height* attribute is missing (in <include>),
an exception will be thrown, which will then cause the child attributes to be used. The exception
throwing mechanism is in **TypedArray#getLayoutDimension(int index, String name)**

Since for PercentFrameLayout & PercentRelativeLayout, it isn't necessary to mention layout_width &
layout_height, no error is thrown during the creation of LayoutParams if those are missing
(a default value of 0 is used). This can be observed in  **PercentLayoutHelper#fetchWidthAndHeight**
which calls **TypedArray#getLayoutDimension(int index, int defValue)**

This leads to the issue of none of the layout attributes of the root view from the included file
being recognized

### FIX
We got to make sure that if appropriate combo of layout_width, layout_height,
layout_widthPercent & layout_heightPercent is not present when creating LayoutParams
from xml attributes, an exception is thrown

#### Override LayoutParams#setBaseAttributes()

```java
@Override
protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
    width = a.getLayoutDimension(widthAttr, Integer.MIN_VALUE);
    height = a.getLayoutDimension(heightAttr, Integer.MIN_VALUE);
}
```

#### Override ViewGroup#generateLayoutParams(AttributeSet attrs)

```java
@Override
public LayoutParams generateLayoutParams(AttributeSet attrs) {
    LayoutParams lp = new LayoutParams(getContext(), attrs);
    if ((lp.width == Integer.MIN_VALUE && lp.getPercentLayoutInfo().widthPercent < 0) ||
            (lp.height == Integer.MIN_VALUE && lp.getPercentLayoutInfo().heightPercent < 0)) {
        throw new UnsupportedOperationException("width or height is missing");
    }

    return lp;
}
```