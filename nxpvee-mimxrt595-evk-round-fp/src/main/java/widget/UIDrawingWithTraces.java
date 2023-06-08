/*
 * Java
 *
 * Copyright 2023 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package widget;

import ej.microui.display.LLUIDisplay;
import ej.microui.display.LLUIPainter.MicroUIGraphicsContext;
import ej.microui.display.LLUIPainter.MicroUIImage;
import ej.microui.display.UIDrawing;
import ej.microui.display.UIDrawing.UIDrawingDefault;

/**
 * Custom {@link UIDrawing} implementations to trace and compute area drawn between flush.
 */
public class UIDrawingWithTraces implements UIDrawingDefault {

	private long areaDrawn;
	private final StringBuilder sb = new StringBuilder();

	private final int displayWidth;
	private final int displayHeight;

	/**
	 * Creates a UIDrawingWithTraces
	 *
	 * @param displayWidth
	 *            the width of the display.
	 * @param displayHeight
	 *            the height of the display.
	 */
	public UIDrawingWithTraces(int displayWidth, int displayHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
	}

	/**
	 * Resets the internal counter and area computation of this drawing. Use this in the flush method.
	 */
	public void reset() {
		this.areaDrawn = 0;
		this.sb.setLength(0);
	}

	@Override
	public void writePixel(MicroUIGraphicsContext gc, int x, int y) {
		sb.append("writePixel x=" + x + " y=" + y + "\n");
		UIDrawingDefault.super.writePixel(gc, x, y);
	}

	@Override
	public void drawLine(MicroUIGraphicsContext gc, int x1, int y1, int x2, int y2) {
		sb.append("drawLine x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2 + "\n");
		UIDrawingDefault.super.drawLine(gc, x1, y1, x2, y2);
	}

	@Override
	public void drawHorizontalLine(MicroUIGraphicsContext gc, int x1, int x2, int y) {
		sb.append("drawHorizontalLine x1=" + x1 + " x2=" + x2 + " y=" + y + "\n");
		UIDrawingDefault.super.drawHorizontalLine(gc, x1, x2, y);
	}

	@Override
	public void drawVerticalLine(MicroUIGraphicsContext gc, int x, int y1, int y2) {
		sb.append("drawVerticalLine x=" + x + " y1=" + y1 + " y2=" + y2 + "\n");
		UIDrawingDefault.super.drawVerticalLine(gc, x, y1, y2);
	}

	@Override
	public void drawRectangle(MicroUIGraphicsContext gc, int x1, int y1, int x2, int y2) {
		sb.append("drawRectangle x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2 + "\n");
		UIDrawingDefault.super.drawRectangle(gc, x1, y1, x2, y2);
	}

	@Override
	public void drawRoundedRectangle(MicroUIGraphicsContext gc, int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		sb.append("drawRoundedRectangle x=" + x + " y=" + y + " width=" + width + " height=" + height + " arcWidth="
				+ arcWidth + " arcHeight=" + arcHeight + "\n");
		UIDrawingDefault.super.drawRoundedRectangle(gc, x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawCircleArc(MicroUIGraphicsContext gc, int x, int y, int diameter, float startAngle, float arcAngle) {
		sb.append("drawCircleArc x=" + x + " y=" + " diameter=" + diameter + " startAngle=" + startAngle + " arcAngle="
				+ arcAngle + "\n");
		UIDrawingDefault.super.drawCircleArc(gc, x, y, diameter, startAngle, arcAngle);
	}

	@Override
	public void drawEllipseArc(MicroUIGraphicsContext gc, int x, int y, int width, int height, float startAngle,
			float arcAngle) {
		sb.append("drawEllipseArc x=" + x + " y=" + y + " width=" + width + " height=" + height + " startAngle="
				+ startAngle + " arcAngle=" + arcAngle + "\n");
		UIDrawingDefault.super.drawEllipseArc(gc, x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillCircleArc(MicroUIGraphicsContext gc, int x, int y, int diameter, float startAngle,
			float arcAngle) {

		float radius = diameter / 2f;
		double area = Math.min(getDrawerArea(), 2f * Math.PI * radius * startAngle / 360);
		this.incAreaDrawn((int) area);
		sb.append("fillCircleArc x=" + x + " y=" + y + " diameter=" + diameter + " startAngle=" + startAngle
				+ " arcAngle=" + arcAngle + " area=" + area + "\n");

		UIDrawingDefault.super.fillCircleArc(gc, x, y, diameter, startAngle, arcAngle);
	}

	@Override
	public void fillEllipseArc(MicroUIGraphicsContext gc, int x, int y, int width, int height, float startAngle,
			float arcAngle) {

		// XXX Do not take into account that this is an ellipse arc and not a rectangle.
		int area = Math.min(width * height, getDrawerArea());
		this.incAreaDrawn(area);
		sb.append("fillEllipseArc x=" + x + " y=" + y + " widht=" + width + " height=" + height + " startAngle="
				+ startAngle + " arcAngle=" + arcAngle + " area=" + area + "\n");
		UIDrawingDefault.super.fillEllipseArc(gc, x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawEllipse(MicroUIGraphicsContext gc, int x, int y, int width, int height) {
		sb.append("drawEllipse x=" + x + " y=" + y + " width=" + width + " height=" + height + "\n");
		UIDrawingDefault.super.drawEllipse(gc, x, y, width, height);
	}

	@Override
	public void fillEllipse(MicroUIGraphicsContext gc, int x, int y, int width, int height) {

		// XXX Do not take into account that this is an ellipse and not a rectangle.
		int area = Math.min(width * height, getDrawerArea());
		this.incAreaDrawn(area);
		sb.append("fillEllipse x=" + x + " y=" + y + " width=" + width + " height=" + height + " area=" + area + "\n");
		UIDrawingDefault.super.fillEllipse(gc, x, y, width, height);
	}

	@Override
	public void drawCircle(MicroUIGraphicsContext gc, int x, int y, int diameter) {
		sb.append("drawCircle x=" + x + " y=" + y + " diameter=" + diameter + "\n");
		UIDrawingDefault.super.drawCircle(gc, x, y, diameter);
	}

	@Override
	public void fillCircle(MicroUIGraphicsContext gc, int x, int y, int diameter) {
		float radius = diameter / 2f;
		double area = Math.min(getDrawerArea(), Math.PI * Math.pow(radius, 2));
		this.incAreaDrawn((int) area);
		sb.append("fillCircle x=" + x + " y=" + y + " diameter=" + diameter + " area=" + area + "\n");
		UIDrawingDefault.super.fillCircle(gc, x, y, diameter);
	}

	@Override
	public void fillRoundedRectangle(MicroUIGraphicsContext gc, int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		// XXX Do not take handle arcWidth but it's shoudl be negligible.
		int area = Math.min(width * height, getDrawerArea());
		sb.append("fillEllipseArc x=" + x + " y=" + y + " widht=" + width + " height=" + height + " arcWidth="
				+ arcWidth + " arcHeight=" + arcHeight + " area=" + area + "\n");
		UIDrawingDefault.super.fillRoundedRectangle(gc, x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawImage(MicroUIGraphicsContext gc, MicroUIImage img, int regionX, int regionY, int width, int height,
			int x, int y, int alpha) {

		// XXX Do not take into account if the image painted goes out of the graphics context.
		int area = Math.min(width * height, getDrawerArea());
		this.incAreaDrawn(area);
		sb.append("drawImage regionX=" + regionX + " regionY=" + regionY + " width=" + width + " height=" + height
				+ " x=" + x + " y=" + y + " alpha=" + alpha + " area=" + area + "\n");
		UIDrawingDefault.super.drawImage(gc, img, regionX, regionY, width, height, x, y, alpha);
	}


	@Override
	public void fillRectangle(MicroUIGraphicsContext gc, int x1, int y1, int x2, int y2) {
		int widthDrawn = Math.abs(x1 - x2);
		int heightDrawn = Math.abs(y1 - y2);
		int area = Math.min(widthDrawn * heightDrawn, getDrawerArea());
		this.incAreaDrawn(area);

		sb.append("fillRectangle x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2=" + y2 + " area=" + area + "\n");
		LLUIDisplay.Instance.getUIDrawerSoftware().fillRectangle(gc, x1, y1, x2, y2);
	}

	/**
	 * @return the area drawn since the last {@link #reset()}.
	 */
	public long getAreaDrawn() {
		return this.areaDrawn;
	}

	public StringBuilder getSB() {
		return this.sb;
	}

	/**
	 * @return the area of the drawer.
	 */
	public int getDrawerArea() {
		// Round display, area is that of a circle.
		return (int) (Math.pow(this.displayWidth / 2, 2) * Math.PI);

	}

	/**
	 * @param areaDrawn
	 *            the area drawn by the primitive..
	 */
	private void incAreaDrawn(int areaDrawn) {
		assert areaDrawn >= 0;
		this.areaDrawn += areaDrawn;
		if (this.areaDrawn < 0) {
			throw new RuntimeException("Area drawn can't be negative. " + this.areaDrawn);
		}
	}
}
