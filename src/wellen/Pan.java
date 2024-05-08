/*
 * Wellen
 *
 * This file is part of the *wellen* library (https://github.com/dennisppaul/wellen).
 * Copyright (c) 2024 Dennis P Paul.
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package wellen;

import wellen.dsp.DSPNodeProcessSignal;
import wellen.dsp.Signal;

import static processing.core.PConstants.HALF_PI;
import static wellen.Wellen.PAN_LINEAR;
import static wellen.Wellen.PAN_SINE_LAW;
import static wellen.Wellen.PAN_SQUARE_LAW;
import static wellen.Wellen.SIGNAL_LEFT;
import static wellen.Wellen.SIGNAL_RIGHT;

/**
 * position a mono signal somewhere in a stereo space.
 */
public class Pan implements DSPNodeProcessSignal {

    private int mPanType;
    private float mPanning;
    private float mPanningNormalized;

    public Pan() {
        set_pan_type(PAN_LINEAR);
        set_panning(0.0f);
    }

    public void set_pan_type(int pPanType) {
        mPanType = pPanType;
    }

    public float get_panning() {
        return mPanning;
    }

    /**
     * set the panning value to position the signal in stereo space.
     *
     * @param pPanning the value ranges from -1.0 to 1.0 where -1.0 is left and 1.0 is right channel.
     */
    public void set_panning(float pPanning) {
        mPanning = (pPanning > 1.0f) ? 1.0f : ((pPanning < -1.0f) ? -1.0f : pPanning);
        mPanningNormalized = (mPanning + 1.0f) * 0.5f;
    }

    public Signal process(float pSignal) {
        final Signal mSignal = Signal.create_stereo(pSignal);
        switch (mPanType) {
            case PAN_LINEAR:
                mSignal.signal[SIGNAL_LEFT] *= 1.0f - mPanningNormalized;
                mSignal.signal[SIGNAL_RIGHT] *= mPanningNormalized;
                break;
            case PAN_SQUARE_LAW:
                mSignal.signal[SIGNAL_LEFT] *= Math.sqrt(1.0f - mPanningNormalized);
                mSignal.signal[SIGNAL_RIGHT] *= Math.sqrt(mPanningNormalized);
                break;
            case PAN_SINE_LAW:
                mSignal.signal[SIGNAL_LEFT] *= Math.sin((1.0f - mPanningNormalized) * HALF_PI);
                mSignal.signal[SIGNAL_RIGHT] *= Math.sin(mPanningNormalized * HALF_PI);
                break;
        }
        return mSignal;
    }

    /**
     * map audio signal into stereo space. mono signals are positioned in stereo space. stereo signals are biased
     * according to the current panning value. signals with more than 2 channels are <em>clipped</em> to 2 channels.
     *
     * @param pSignal incoming signal with 1 or more channels
     * @return processed signal
     */
    @Override
    public Signal process_signal(Signal pSignal) {
        final Signal mSignal;
        if (pSignal.is_mono()) {
            mSignal = Signal.create_stereo(pSignal.mono());
        } else if (pSignal.is_stereo() || pSignal.num_channels() > 2) {
            mSignal = new Signal(pSignal);
        } else if (pSignal.num_channels() > 2) {
            mSignal = new Signal(2);
            mSignal.set(pSignal);
        } else {
            mSignal = new Signal();
        }
        return applyPanning(mSignal);
    }

    private Signal applyPanning(Signal mSignal) {
        switch (mPanType) {
            case PAN_LINEAR:
                mSignal.signal[SIGNAL_LEFT] *= 1.0f - mPanningNormalized;
                mSignal.signal[SIGNAL_RIGHT] *= mPanningNormalized;
                break;
            case PAN_SQUARE_LAW:
                mSignal.signal[SIGNAL_LEFT] *= Math.sqrt(1.0f - mPanningNormalized);
                mSignal.signal[SIGNAL_RIGHT] *= Math.sqrt(mPanningNormalized);
                break;
            case PAN_SINE_LAW:
                mSignal.signal[SIGNAL_LEFT] *= Math.sin((1.0f - mPanningNormalized) * HALF_PI);
                mSignal.signal[SIGNAL_RIGHT] *= Math.sin(mPanningNormalized * HALF_PI);
                break;
        }
        return mSignal;
    }
}
