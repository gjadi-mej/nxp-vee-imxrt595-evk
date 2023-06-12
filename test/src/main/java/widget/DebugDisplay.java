/*
 * Java
 *
 * Copyright 2023 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package widget;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import ej.fp.Device;
import ej.fp.Image;
import ej.fp.MouseListener;
import ej.fp.Widget.WidgetAttribute;
import ej.fp.Widget.WidgetDescription;
import ej.fp.widget.Display;
import ej.microui.display.LLUIPainter;
import ej.microui.display.LLUIPainter.MicroUIGraphicsContext;

/**
 * Same widget than {@link Display} but draw a tool tip on display pixel
 * coordinates when user clicking on mouse third button (often right click).
 * <p>
 * Contrary to {@link Display} this display implements {@link MouseListener} to
 * be able to catch mouse events and position.
 */
@WidgetDescription(attributes = { @WidgetAttribute(name = "label", isOptional = true), @WidgetAttribute(name = "x"),
		@WidgetAttribute(name = "y"), @WidgetAttribute(name = "width"), @WidgetAttribute(name = "height"),
		@WidgetAttribute(name = "displayWidth", isOptional = true),
		@WidgetAttribute(name = "displayHeight", isOptional = true),
		@WidgetAttribute(name = "initialColor", isOptional = true), @WidgetAttribute(name = "alpha", isOptional = true),
		@WidgetAttribute(name = "doubleBufferFeature", isOptional = true),
		@WidgetAttribute(name = "backlightFeature", isOptional = true),
		@WidgetAttribute(name = "filter", isOptional = true),
		@WidgetAttribute(name = "extensionClass", isOptional = true) })
public class DebugDisplay extends Display implements MouseListener {

	public UIDrawingWithTraces drawer;

	private boolean showCoordinates;
	private int x, y;

	private int displayWidth;
	private int displayHeight;

	private final File rootDir = new File("C:\\UIFlushVisualizer\\");
	private final File flushDir = new File(rootDir, "flush");
	private final File summaryFile = new File(rootDir, "summary.txt");
	private final File fullOpFile = new File(rootDir, "fullOp.txt");
	private final File confJs = new File(flushDir, "conf.js");

	private PrintStream summary;

	private int flushCount;

	private PrintStream fullOp;

	private int nOfFlush;

	public DebugDisplay() {
		this.showCoordinates = true;
	}

	@Override
	public void start() {
		super.start();

		System.out.println("summary=" + summaryFile.getAbsolutePath());
		System.out.println("fullOp=" + fullOpFile.getAbsolutePath());
		flushDir.mkdirs();
		try {
			this.summary = new PrintStream(new FileOutputStream(summaryFile));
			this.fullOp = new PrintStream(new FileOutputStream(fullOpFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// XXX Is this the correct way to get the width/height of the display? I don't
		// know how I can get it from the width/height @WidgetAttribute.
		Image skin = super.getCurrentSkin();
		this.displayWidth = skin.getWidth();
		this.displayHeight = skin.getHeight();
		this.drawer = new UIDrawingWithTraces(displayWidth, displayHeight);
		LLUIPainter.setDrawer(drawer);
		System.out.println("Drawer Area: " + drawer.getDrawerArea());
		summary.append("Drawer Area: " + drawer.getDrawerArea() + "\n");

		try (InputStream input = this.getClass().getClassLoader()
				.getResourceAsStream("UIFlushVisualizer.html")) {
			Files.copy(input, Path.of(rootDir.getAbsolutePath(), "UIFlushVisualizer.html"),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized Image getCurrentSkin() {
		Image skin = super.getCurrentSkin();
		if (showCoordinates) {
			skin.drawString(x + "," + y, x + 10, y, 0xff000000, 0xffe9ed7b);
		}
		return skin;
	}

	@Override
	public void mouseDragged(int x, int y) {
		mouseMoved(x, y);
	}

	@Override
	public void mouseMoved(int x, int y) {
		this.x = x;
		this.y = y;
		if (showCoordinates) {
			Device.getDevice().repaint();
		}
	}

	@Override
	public void mousePressed(int x, int y, MouseButton button) {
		if (button == MouseButton.THIRD_BUTTON) {
			this.showCoordinates = !showCoordinates;

			// repaint all device to erase potential pointer tool tip which can be outside
			// the display
			Device.getDevice().repaint();
		}
	}

	@Override
	public void flush(MicroUIGraphicsContext gc, Image image, int x, int y, int width, int height) {
		super.flush(gc, image, x, y, width, height);
		BufferedImage rawImage = (BufferedImage) gc.getImage().getRAWImage();
		File outputImg = new File(flushDir, "flush-" + flushCount + ".png");
		try {
			ImageIO.write(rawImage, "png", outputImg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String flushStr = flushCount + "# Area drawn: " + (100L * drawer.getAreaDrawn() / drawer.getDrawerArea()) + "%";
		System.out.println(flushStr);
		summary.println(flushStr);
		StringBuilder sb = drawer.getSB();
		sb.append(flushStr);

		File outputTxt = new File(flushDir, "flush-" + flushCount + ".txt");
		try (PrintStream output = new PrintStream(new FileOutputStream(outputTxt))) {
			output.print(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		this.nOfFlush++;
		try (PrintStream output = new PrintStream(new FileOutputStream(confJs))) {
			output.print("function init() { window.total = " + this.nOfFlush + "; }");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		sb.append("\n----------\n");
		fullOp.append(sb.toString());
		drawer.reset();
		flushCount++;

	}

	public Image getFrameBuffer() {
		return frameBuffer;
	}

}
