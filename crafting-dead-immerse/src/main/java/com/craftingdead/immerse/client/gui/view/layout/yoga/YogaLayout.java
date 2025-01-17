/*
 * Crafting Dead
 * Copyright (C) 2022  NexusNode LTD
 *
 * This Non-Commercial Software License Agreement (the "Agreement") is made between you (the "Licensee") and NEXUSNODE (BRAD HUNTER). (the "Licensor").
 * By installing or otherwise using Crafting Dead (the "Software"), you agree to be bound by the terms and conditions of this Agreement as may be revised from time to time at Licensor's sole discretion.
 *
 * If you do not agree to the terms and conditions of this Agreement do not download, copy, reproduce or otherwise use any of the source code available online at any time.
 *
 * https://github.com/nexusnode/crafting-dead/blob/1.18.x/LICENSE.txt
 *
 * https://craftingdead.net/terms.php
 */

package com.craftingdead.immerse.client.gui.view.layout.yoga;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.util.yoga.YGMeasureFunc;
import org.lwjgl.util.yoga.YGSize;
import org.lwjgl.util.yoga.Yoga;
import com.craftingdead.immerse.client.gui.view.Overflow;
import com.craftingdead.immerse.client.gui.view.Point;
import com.craftingdead.immerse.client.gui.view.layout.Layout;
import com.craftingdead.immerse.client.gui.view.layout.MeasureMode;
import com.craftingdead.immerse.client.gui.view.property.StyleableProperty;
import com.craftingdead.immerse.client.gui.view.property.ValueAccessor;
import com.craftingdead.immerse.client.gui.view.state.StateListener;
import com.craftingdead.immerse.client.gui.view.style.PropertyDispatcher;
import com.craftingdead.immerse.client.gui.view.style.shorthand.ShorthandArgMapper;
import com.craftingdead.immerse.client.gui.view.style.shorthand.ShorthandDispatcher;

public class YogaLayout implements Layout {

  /**
   * Maps a {@link Yoga} YGMeasureMode to a {@link MeasureMode}.
   */
  private static final MeasureMode[] measureModes =
      new MeasureMode[] {MeasureMode.UNDEFINED, MeasureMode.EXACTLY, MeasureMode.AT_MOST};

  /**
   * Maps a {@link Yoga} YGMeasureMode to a {@link MeasureMode}.
   */
  private static final Overflow[] overflows =
      new Overflow[] {Overflow.VISIBLE, Overflow.HIDDEN, Overflow.SCROLL};

  private final StyleableProperty<Float> borderTopWidth;
  private final StyleableProperty<Float> borderRightWidth;
  private final StyleableProperty<Float> borderBottomWidth;
  private final StyleableProperty<Float> borderLeftWidth;
  private final PropertyDispatcher<Float> borderWidth;

  private final StyleableProperty<Point> top;
  private final StyleableProperty<Point> right;
  private final StyleableProperty<Point> bottom;
  private final StyleableProperty<Point> left;
  private final PropertyDispatcher<Point> inset;

  private final StyleableProperty<Point> paddingTop;
  private final StyleableProperty<Point> paddingRight;
  private final StyleableProperty<Point> paddingBottom;
  private final StyleableProperty<Point> paddingLeft;
  private final PropertyDispatcher<Point> padding;
  private final StyleableProperty<Point> marginTop;
  private final StyleableProperty<Point> marginRight;
  private final StyleableProperty<Point> marginBottom;
  private final StyleableProperty<Point> marginLeft;
  private final PropertyDispatcher<Point> margin;
  private final StyleableProperty<YogaPositionType> position;

  private final StyleableProperty<Float> flexGrow;
  private final StyleableProperty<Float> flexShrink;
  private final StyleableProperty<Point> flexBasis;
  private final StyleableProperty<Float> flex;

  private final StyleableProperty<Float> aspectRatio;

  private final StyleableProperty<YogaAlign> alignSelf;

  private final StyleableProperty<Point> width;

  private final StyleableProperty<Point> height;

  private final StyleableProperty<Point> minWidth;

  private final StyleableProperty<Point> minHeight;

  private final StyleableProperty<Overflow> overflow;

  final long node;

  private boolean measureFunctionPresent;
  private boolean closed;

  public YogaLayout() {
    this.node = Yoga.YGNodeNew();

    this.borderTopWidth = StyleableProperty.create("border-top-width", Float.class,
        0.0F, this::setTopBorderWidth);
    this.borderRightWidth = StyleableProperty.create("border-right-width", Float.class,
        0.0F, this::setRightBorderWidth);
    this.borderBottomWidth = StyleableProperty.create("border-bottom-width", Float.class,
        0.0F, this::setBottomBorderWidth);
    this.borderLeftWidth = StyleableProperty.create("border-left-width", Float.class,
        0.0F, this::setLeftBorderWidth);
    this.borderWidth =
        ShorthandDispatcher.create("border-width", Float.class, ShorthandArgMapper.BOX_MAPPER,
            this.borderTopWidth, this.borderRightWidth, this.borderBottomWidth,
            this.borderLeftWidth);

    this.top = StyleableProperty.create("top", Point.class, Point.ZERO,
        value -> value.dispatch(this::setTop, this::setTopPercent, null));
    this.right = StyleableProperty.create("right", Point.class, Point.ZERO,
        value -> value.dispatch(this::setRight, this::setRightPercent, null));
    this.bottom = StyleableProperty.create("bottom", Point.class, Point.ZERO,
        value -> value.dispatch(this::setBottom, this::setBottomPercent, null));
    this.left = StyleableProperty.create("left", Point.class, Point.ZERO,
        value -> value.dispatch(this::setLeft, this::setLeftPercent, null));
    this.inset = ShorthandDispatcher.create("inset", Point.class, ShorthandArgMapper.BOX_MAPPER,
        this.top, this.right, this.bottom, this.left);

    this.paddingTop = StyleableProperty.create("padding-top", Point.class, Point.ZERO,
        value -> value.dispatch(this::setTopPadding, this::setTopPaddingPercent, null));
    this.paddingRight = StyleableProperty.create("padding-right", Point.class, Point.ZERO,
        value -> value.dispatch(this::setRightPadding, this::setRightPaddingPercent, null));
    this.paddingBottom = StyleableProperty.create("padding-bottom", Point.class, Point.ZERO,
        value -> value.dispatch(this::setBottomPadding, this::setBottomPaddingPercent, null));
    this.paddingLeft = StyleableProperty.create("padding-left", Point.class, Point.ZERO,
        value -> value.dispatch(this::setLeftPadding, this::setLeftPaddingPercent, null));
    this.padding = ShorthandDispatcher.create("padding", Point.class, ShorthandArgMapper.BOX_MAPPER,
        this.paddingTop, this.paddingRight, this.paddingBottom, this.paddingLeft);

    this.marginTop = StyleableProperty.create("margin-top", Point.class, Point.ZERO,
        value -> value.dispatch(this::setTopMargin, this::setTopMarginPercent,
            this::setTopMarginAuto));
    this.marginRight = StyleableProperty.create("margin-right", Point.class, Point.ZERO,
        value -> value.dispatch(this::setRightMargin, this::setRightMarginPercent,
            this::setRightMarginAuto));
    this.marginBottom = StyleableProperty.create("margin-bottom", Point.class, Point.ZERO,
        value -> value.dispatch(this::setBottomMargin, this::setBottomMarginPercent,
            this::setBottomMarginAuto));
    this.marginLeft = StyleableProperty.create("margin-left", Point.class, Point.ZERO,
        value -> value.dispatch(this::setLeftMargin, this::setLeftMarginPercent,
            this::setLeftMarginAuto));
    this.margin = ShorthandDispatcher.create("margin", Point.class, ShorthandArgMapper.BOX_MAPPER,
        this.marginTop, this.marginRight, this.marginBottom, this.marginLeft);

    this.position = StyleableProperty.create("position", YogaPositionType.class,
        YogaPositionType.RELATIVE, this::setPositionType);

    this.flexGrow = StyleableProperty.create("flex-grow", Float.class,
        0.0F, this::setFlexGrow);
    this.flexShrink = StyleableProperty.create("flex-shrink", Float.class,
        1.0F, this::setFlexShrink);
    this.flexBasis = StyleableProperty.create("flex-basis", Point.class,
        Point.AUTO, value -> value.dispatch(this::setFlexBasis, this::setFlexBasisPercent,
            this::setFlexBasisAuto));
    this.flex = new StyleableProperty<>("flex", Float.class,
        ValueAccessor.getterSetter(this::getFlex, this::setFlex, null));

    this.aspectRatio = new StyleableProperty<>("aspect-ratio", Float.class,
        ValueAccessor.getterSetter(this::getAspectRatio, this::setAspectRatio, null));

    this.alignSelf = StyleableProperty.create("align-self", YogaAlign.class, YogaAlign.AUTO,
        this::setAlignSelf);

    this.width = StyleableProperty.create("width", Point.class, Point.AUTO,
        value -> value.dispatch(this::setWidth, this::setWidthPercent, this::setWidthAuto));

    this.height = StyleableProperty.create("height", Point.class, Point.AUTO,
        value -> value.dispatch(this::setHeight, this::setHeightPercent, this::setHeightAuto));

    this.minWidth = StyleableProperty.create("min-width", Point.class, Point.ZERO,
        value -> value.dispatch(this::setMinWidth, this::setMinWidthPercent, null));

    this.minHeight = StyleableProperty.create("min-height", Point.class, Point.ZERO,
        value -> value.dispatch(this::setMinHeight, this::setMinHeightPercent, null));

    this.overflow = StyleableProperty.create("overflow", Overflow.class,
        Overflow.VISIBLE, this::setOverflow);
  }

  public final YogaLayout setOverflow(Overflow overflow) {
    this.checkClosed();
    for (int i = 0; i < overflows.length; i++) {
      if (overflow == overflows[i]) {
        Yoga.YGNodeStyleSetOverflow(this.node, i);
        return this;
      }
    }
    throw new IllegalStateException("Unknown value: " + overflow);
  }

  public final YogaLayout setBorderWidth(float width) {
    this.checkClosed();
    Yoga.YGNodeStyleSetBorder(this.node, Yoga.YGEdgeAll, width);
    return this;
  }

  public final YogaLayout setLeftBorderWidth(float width) {
    this.checkClosed();
    Yoga.YGNodeStyleSetBorder(this.node, Yoga.YGEdgeLeft, width);
    return this;
  }

  public final YogaLayout setRightBorderWidth(float width) {
    this.checkClosed();
    Yoga.YGNodeStyleSetBorder(this.node, Yoga.YGEdgeRight, width);
    return this;
  }

  public final YogaLayout setTopBorderWidth(float width) {
    this.checkClosed();
    Yoga.YGNodeStyleSetBorder(this.node, Yoga.YGEdgeTop, width);
    return this;
  }

  public final YogaLayout setBottomBorderWidth(float width) {
    this.checkClosed();
    Yoga.YGNodeStyleSetBorder(this.node, Yoga.YGEdgeBottom, width);
    return this;
  }

  public final YogaLayout setLeft(float left) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPosition(this.node, Yoga.YGEdgeLeft, left);
    return this;
  }

  public final YogaLayout setLeftPercent(float leftPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPositionPercent(this.node, Yoga.YGEdgeLeft, leftPercent);
    return this;
  }

  public final YogaLayout setRight(float right) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPosition(this.node, Yoga.YGEdgeRight, right);
    return this;
  }

  public final YogaLayout setRightPercent(float rightPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPositionPercent(this.node, Yoga.YGEdgeRight, rightPercent);
    return this;
  }

  public final YogaLayout setTop(float top) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPosition(this.node, Yoga.YGEdgeTop, top);
    return this;
  }

  public final YogaLayout setTopPercent(float topPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPositionPercent(this.node, Yoga.YGEdgeTop, topPercent);
    return this;
  }

  public final YogaLayout setBottom(float bottom) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPosition(this.node, Yoga.YGEdgeBottom, bottom);
    return this;
  }

  public final YogaLayout setBottomPercent(float bottomPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPositionPercent(this.node, Yoga.YGEdgeBottom, bottomPercent);
    return this;
  }

  public final YogaLayout setLeftPadding(float leftPadding) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPadding(this.node, Yoga.YGEdgeLeft, leftPadding);
    return this;
  }

  public final YogaLayout setLeftPaddingPercent(float leftPaddingPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPaddingPercent(this.node, Yoga.YGEdgeLeft, leftPaddingPercent);
    return this;
  }

  public final YogaLayout setRightPadding(float rightPadding) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPadding(this.node, Yoga.YGEdgeRight, rightPadding);
    return this;
  }

  public final YogaLayout setRightPaddingPercent(float rightPaddingPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPaddingPercent(this.node, Yoga.YGEdgeRight, rightPaddingPercent);
    return this;
  }

  public final YogaLayout setTopPadding(float topPadding) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPadding(this.node, Yoga.YGEdgeTop, topPadding);
    return this;
  }

  public final YogaLayout setTopPaddingPercent(float topPaddingPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPaddingPercent(this.node, Yoga.YGEdgeTop, topPaddingPercent);
    return this;
  }

  public final YogaLayout setBottomPadding(float bottomPadding) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPadding(this.node, Yoga.YGEdgeBottom, bottomPadding);
    return this;
  }

  public final YogaLayout setBottomPaddingPercent(float bottomPaddingPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPaddingPercent(this.node, Yoga.YGEdgeBottom, bottomPaddingPercent);
    return this;
  }

  public final YogaLayout setPadding(float padding) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPadding(this.node, Yoga.YGEdgeAll, padding);
    return this;
  }

  public final YogaLayout setPaddingPercent(float paddingPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPaddingPercent(this.node, Yoga.YGEdgeAll, paddingPercent);
    return this;
  }

  public final YogaLayout setLeftMargin(float leftMargin) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMargin(this.node, Yoga.YGEdgeLeft, leftMargin);
    return this;
  }

  public final YogaLayout setLeftMarginPercent(float leftMarginPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginPercent(this.node, Yoga.YGEdgeLeft, leftMarginPercent);
    return this;
  }

  public final YogaLayout setRightMargin(float rightMargin) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMargin(this.node, Yoga.YGEdgeRight, rightMargin);
    return this;
  }

  public final YogaLayout setRightMarginPercent(float rightMarginPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginPercent(this.node, Yoga.YGEdgeRight, rightMarginPercent);
    return this;
  }

  public final YogaLayout setTopMargin(float topMargin) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMargin(this.node, Yoga.YGEdgeTop, topMargin);
    return this;
  }

  public final YogaLayout setTopMarginPercent(float topMarginPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginPercent(this.node, Yoga.YGEdgeTop, topMarginPercent);
    return this;
  }

  public final YogaLayout setBottomMargin(float bottomMargin) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMargin(this.node, Yoga.YGEdgeBottom, bottomMargin);
    return this;
  }

  public final YogaLayout setBottomMarginPercent(float bottomMarginPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginPercent(this.node, Yoga.YGEdgeBottom, bottomMarginPercent);
    return this;
  }

  public final YogaLayout setMargin(float margin) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMargin(this.node, Yoga.YGEdgeAll, margin);
    return this;
  }

  public final YogaLayout setMarginPercent(float marginPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginPercent(this.node, Yoga.YGEdgeAll, marginPercent);
    return this;
  }

  public final YogaLayout setLeftMarginAuto() {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginAuto(this.node, Yoga.YGEdgeLeft);
    return this;
  }

  public final YogaLayout setRightMarginAuto() {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginAuto(this.node, Yoga.YGEdgeRight);
    return this;
  }

  public final YogaLayout setTopMarginAuto() {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginAuto(this.node, Yoga.YGEdgeTop);
    return this;
  }

  public final YogaLayout setBottomMarginAuto() {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginAuto(this.node, Yoga.YGEdgeBottom);
    return this;
  }

  public final YogaLayout setMarginAuto() {
    this.checkClosed();
    Yoga.YGNodeStyleSetMarginAuto(this.node, Yoga.YGEdgeAll);
    return this;
  }

  public final YogaLayout setPositionType(YogaPositionType positionType) {
    this.checkClosed();
    Yoga.YGNodeStyleSetPositionType(this.node, positionType == YogaPositionType.ABSOLUTE
        ? Yoga.YGPositionTypeAbsolute
        : Yoga.YGPositionTypeRelative);
    return this;
  }

  public final YogaLayout setFlexGrow(float flexGrow) {
    this.checkClosed();
    Yoga.YGNodeStyleSetFlexGrow(this.node, flexGrow);
    return this;
  }

  public final YogaLayout setFlexShrink(float flexShrink) {
    this.checkClosed();
    Yoga.YGNodeStyleSetFlexShrink(this.node, flexShrink);
    return this;
  }

  public final YogaLayout setFlexBasis(float flexBasis) {
    this.checkClosed();
    Yoga.YGNodeStyleSetFlexBasis(this.node, flexBasis);
    return this;
  }

  public final YogaLayout setFlexBasisPercent(float flexBasisPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetFlexBasisPercent(this.node, flexBasisPercent);
    return this;
  }

  public final YogaLayout setFlexBasisAuto() {
    this.checkClosed();
    Yoga.YGNodeStyleSetFlexBasisAuto(this.node);
    return this;
  }

  public final float getFlex() {
    this.checkClosed();
    return Yoga.YGNodeStyleGetFlex(this.node);
  }

  public final YogaLayout setFlex(float flex) {
    this.checkClosed();
    Yoga.YGNodeStyleSetFlex(this.node, flex);
    return this;
  }

  public final float getAspectRatio() {
    this.checkClosed();
    return Yoga.YGNodeStyleGetAspectRatio(this.node);
  }

  public final YogaLayout setAspectRatio(float aspectRatio) {
    this.checkClosed();
    Yoga.YGNodeStyleSetAspectRatio(this.node, aspectRatio);
    return this;
  }

  public final YogaLayout setAlignSelf(YogaAlign align) {
    this.checkClosed();
    Yoga.YGNodeStyleSetAlignSelf(this.node, align.getYogaType());
    return this;
  }

  public final YogaLayout setWidth(float width) {
    this.checkClosed();
    Yoga.YGNodeStyleSetWidth(this.node, width);
    return this;
  }

  public final YogaLayout setWidthPercent(float widthPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetWidthPercent(this.node, widthPercent);
    return this;
  }

  public final YogaLayout setWidthAuto() {
    this.checkClosed();
    Yoga.YGNodeStyleSetWidthAuto(this.node);
    return this;
  }

  public YogaLayout setMaxWidth(float maxWidth) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMaxWidth(this.node, maxWidth);
    return this;
  }

  public YogaLayout setMaxWidthPercent(float maxWidthPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMaxWidthPercent(this.node, maxWidthPercent);
    return this;
  }

  public final YogaLayout setHeight(float height) {
    this.checkClosed();
    Yoga.YGNodeStyleSetHeight(this.node, height);
    return this;
  }

  public final YogaLayout setHeightPercent(float heightPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetHeightPercent(this.node, heightPercent);
    return this;
  }

  public final YogaLayout setHeightAuto() {
    this.checkClosed();
    Yoga.YGNodeStyleSetHeightAuto(this.node);
    return this;
  }

  public YogaLayout setMaxHeight(float maxHeight) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMaxHeight(this.node, maxHeight);
    return this;
  }

  public YogaLayout setMaxHeightPercent(float maxHeightPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMaxHeightPercent(this.node, maxHeightPercent);
    return this;
  }

  public YogaLayout setMinWidth(float minWidth) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMinWidth(this.node, minWidth);
    return this;
  }

  public YogaLayout setMinWidthPercent(float minWidthPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMinWidthPercent(this.node, minWidthPercent);
    return this;
  }

  public YogaLayout setMinHeight(float minHeight) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMinHeight(this.node, minHeight);
    return this;
  }

  public YogaLayout setMinHeightPercent(float minHeightPercent) {
    this.checkClosed();
    Yoga.YGNodeStyleSetMinHeightPercent(this.node, minHeightPercent);
    return this;
  }

  @Override
  public Overflow getOverflow() {
    this.checkClosed();
    return overflows[Yoga.YGNodeStyleGetOverflow(this.node)];
  }

  @Override
  public float getLeft() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetLeft(this.node);
  }

  @Override
  public float getLeftPadding() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetPadding(this.node, Yoga.YGEdgeLeft);
  }

  @Override
  public float getLeftBorder() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetBorder(this.node, Yoga.YGEdgeLeft);
  }

  @Override
  public float getRight() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetRight(this.node);
  }

  @Override
  public float getRightPadding() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetPadding(this.node, Yoga.YGEdgeRight);
  }

  @Override
  public float getRightBorder() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetBorder(this.node, Yoga.YGEdgeRight);
  }

  @Override
  public float getTop() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetTop(this.node);
  }

  @Override
  public float getTopPadding() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetPadding(this.node, Yoga.YGEdgeTop);
  }

  @Override
  public float getTopBorder() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetBorder(this.node, Yoga.YGEdgeTop);
  }

  @Override
  public float getBottom() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetBottom(this.node);
  }

  @Override
  public float getBottomPadding() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetPadding(this.node, Yoga.YGEdgeBottom);
  }

  @Override
  public float getBottomBorder() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetBorder(this.node, Yoga.YGEdgeBottom);
  }

  @Override
  public float getWidth() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetWidth(this.node);
  }

  @Override
  public float getHeight() {
    this.checkClosed();
    return Yoga.YGNodeLayoutGetHeight(this.node);
  }

  @Override
  public void markDirty() {
    this.checkClosed();
    if (!this.measureFunctionPresent) {
      throw new IllegalStateException(
          "Layout must have a measure function in order to mark it as dirty.");
    }
    Yoga.YGNodeMarkDirty(this.node);
  }

  @Override
  public void close() {
    this.checkClosed();
    this.closed = true;
    Yoga.YGNodeFree(this.node);
  }

  private void checkClosed() {
    if (this.closed) {
      throw new IllegalStateException("Layout has been closed.");
    }
  }

  @Override
  public void gatherDispatchers(Consumer<PropertyDispatcher<?>> consumer) {
    consumer.accept(this.borderWidth);
    consumer.accept(this.inset);
    consumer.accept(this.padding);
    consumer.accept(this.margin);
    this.gatherStyleProperties(consumer);
  }

  @Override
  public void gatherListeners(Consumer<StateListener> consumer) {
    this.gatherStyleProperties(consumer);
  }

  private void gatherStyleProperties(Consumer<? super StyleableProperty<?>> consumer) {
    consumer.accept(this.overflow);
    consumer.accept(this.borderTopWidth);
    consumer.accept(this.borderRightWidth);
    consumer.accept(this.borderBottomWidth);
    consumer.accept(this.borderLeftWidth);
    consumer.accept(this.top);
    consumer.accept(this.right);
    consumer.accept(this.bottom);
    consumer.accept(this.left);
    consumer.accept(this.paddingTop);
    consumer.accept(this.paddingRight);
    consumer.accept(this.paddingBottom);
    consumer.accept(this.paddingLeft);
    consumer.accept(this.marginTop);
    consumer.accept(this.marginRight);
    consumer.accept(this.marginBottom);
    consumer.accept(this.marginLeft);
    consumer.accept(this.position);
    consumer.accept(this.flexGrow);
    consumer.accept(this.flexShrink);
    consumer.accept(this.flexBasis);
    consumer.accept(this.flex);
    consumer.accept(this.aspectRatio);
    consumer.accept(this.alignSelf);
    consumer.accept(this.width);
    consumer.accept(this.height);
    consumer.accept(this.minWidth);
    consumer.accept(this.minHeight);
  }

  @Override
  public void setMeasureFunction(@Nullable MeasureFunction measureFunction) {
    this.checkClosed();
    this.measureFunctionPresent = measureFunction != null;
    Yoga.YGNodeSetMeasureFunc(this.node,
        measureFunction == null ? null : (node, width, widthMode, height, heightMode) -> {
          var size = measureFunction.measure(measureModes[widthMode], width,
              measureModes[heightMode], height);
          return YGMeasureFunc.toLong(YGSize.create()
              .width(size.x)
              .height(size.y));
        });
  }
}
