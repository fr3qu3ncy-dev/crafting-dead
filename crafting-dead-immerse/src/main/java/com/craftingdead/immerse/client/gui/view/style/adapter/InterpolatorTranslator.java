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

package com.craftingdead.immerse.client.gui.view.style.adapter;

import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;

public class InterpolatorTranslator
    implements StyleDecoder<Interpolator>, StyleValidator<Interpolator> {

  public static final Interpolator EASE = new SplineInterpolator(0.25D, 0.1D, 0.25D, 1.0D);
  public static final Interpolator EASE_IN = new SplineInterpolator(0.42D, 0.0D, 1.0D, 1.0D);
  public static final Interpolator EASE_OUT = new SplineInterpolator(0.0D, 0.0D, 0.58D, 1.0D);
  public static final Interpolator EASE_IN_OUT = new SplineInterpolator(0.42D, 0.0D, 0.58D, 1.0D);

  @Override
  public Interpolator decode(String style) {
    if (style.equals("ease")) {
      return EASE;
    } else if (style.equals("linear")) {
      return LinearInterpolator.getInstance();
    } else if (style.equals("ease-in")) {
      return EASE_IN;
    } else if (style.equals("ease-out")) {
      return EASE_OUT;
    } else if (style.equals("ease-in-out")) {
      return EASE_IN_OUT;
    }

    if (style.startsWith("cubic-bezier")) {
      var points =
          style.substring(style.indexOf('('), style.indexOf(')')).replace(" ", "").split(",");

      var pointsArray = new double[4];
      for (int i = 0; i < pointsArray.length; i++) {
        pointsArray[i] = Double.parseDouble(points[i]);
      }

      return new SplineInterpolator(pointsArray[0], pointsArray[1], pointsArray[2], pointsArray[3]);
    }

    throw new IllegalStateException("Unsupported timing function: " + style);
  }

  @Override
  public int validate(String style) {
    if (style.startsWith("ease")) {
      return "ease".length();
    } else if (style.startsWith("linear")) {
      return "linear".length();
    } else if (style.startsWith("ease-in")) {
      return "ease-in".length();
    } else if (style.startsWith("ease-out")) {
      return "ease-out".length();
    } else if (style.startsWith("ease-in-out")) {
      return "ease-in-out".length();
    }

    if (style.startsWith("cubic-bezier")) {
      return style.indexOf(')') + 1;
    }

    throw new IllegalStateException("Unsupported timing function: " + style);
  }
}
