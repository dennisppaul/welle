/*
 * Wellen
 *
 * This file is part of the *wellen* library (https://github.com/dennisppaul/wellen).
 * Copyright (c) 2020 Dennis P Paul.
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

import processing.core.PApplet;

/**
 * plays back an array of samples at different speeds.
 */
public class Sampler implements DSPNodeOutput {

    private final float mSamplingRate;
    private float[] mData;
    private float mFrequency;
    private float mStepSize;
    private float mArrayPtr;
    private float mAmplitude;
    private boolean mLoop = false;
    private boolean mDirectionForward;
    private float mSpeed;
    private boolean mInterpolateSamples;
    private int mIn = 0;
    private int mOut = 0;

    public Sampler() {
        this(0);
    }

    public Sampler(int pWavetableSize) {
        this(new float[pWavetableSize], Wellen.DEFAULT_SAMPLING_RATE);
    }

    public Sampler(float[] pWavetable) {
        this(pWavetable, Wellen.DEFAULT_SAMPLING_RATE);
    }

    public Sampler(float[] pWavetable, float pSamplingRate) {
        mData = pWavetable;
        mSamplingRate = pSamplingRate;
        mArrayPtr = 0;
        mInterpolateSamples = false;
        set_speed(1.0f);
        set_amplitude(1.0f);
        set_in(0);
        set_out(mData.length - 1);
    }


    /**
     * load the sample buffer from *raw* byte data. the method assumes a raw format with 32bit float in a value range
     * from -1.0 to 1.0. from -1.0 to 1.0.
     *
     * @param pData raw byte data ( assuming 4 bytes per sample, 32-bit float aka WAVE_FORMAT_IEEE_FLOAT_32BIT
     *         )
     * @return instance with data loaded
     */
    public Sampler load(byte[] pData) {
        load(pData, true);
        return this;
    }

    /**
     * load the sample buffer from *raw* byte data. the method assumes a raw format with 32bit float in a value range
     * from -1.0 to 1.0.
     *
     * @param pData raw byte data ( assuming 4 bytes per sample, 32-bit float aka WAVE_FORMAT_IEEE_FLOAT_32BIT
     *         )
     * @param pLittleEndian true if byte data is arranged in little endian order
     * @return instance with data loaded
     */
    public Sampler load(byte[] pData, boolean pLittleEndian) {
        if (mData == null || mData.length != pData.length / 4) {
            mData = new float[pData.length / 4];
        }
        Wellen.bytes_to_floatIEEEs(pData, data(), pLittleEndian);
        rewind();
        set_speed(mSpeed);
        set_in(0);
        set_out(mData.length - 1);
        return this;
    }

    public int get_in() {
        return mIn;
    }

    public void set_in(int pIn) {
        if (pIn > mOut) {
            pIn = mOut;
        }
        mIn = pIn;
    }

    public int get_out() {
        return mOut;
    }

    public void set_out(int pOut) {
        mOut = pOut > last_index() ? last_index() : (pOut < mIn ? mIn : pOut);
    }

    public float get_speed() {
        return mSpeed;
    }

    public void set_speed(float pSpeed) {
        mSpeed = pSpeed;
        mDirectionForward = pSpeed > 0;
        set_frequency(PApplet.abs(pSpeed) * mSamplingRate / data().length); /* aka `mStepSize = pSpeed;` */
    }

    public void set_frequency(float pFrequency) {
        if (mFrequency != pFrequency) {
            mFrequency = pFrequency;
            mStepSize = mFrequency * ((float) mData.length / mSamplingRate);
        }
    }

    public void set_amplitude(float pAmplitude) {
        mAmplitude = pAmplitude;
    }

    public float[] data() {
        return mData;
    }

    public void set_data(float[] pData) {
        mData = pData;
        rewind();
        set_speed(mSpeed);
    }

    public void interpolate_samples(boolean pInterpolateSamples) {
        mInterpolateSamples = pInterpolateSamples;
    }

    public int get_position() {
        return (int) mArrayPtr;
    }

    public float output() {
        mArrayPtr += mDirectionForward ? mStepSize : -mStepSize;
        final int i = (int) mArrayPtr;
        if (mData.length == 0 || mDirectionForward ? (i > mOut && !mLoop) : (i < mIn && !mLoop)) {
            return 0.0f;
        }
        final float mFrac = mArrayPtr - i;
        final int j = wrapIndex(i);
        mArrayPtr = j + mFrac;

        if (mInterpolateSamples) {
            final int mNextIndex = wrapIndex(j + 1);
            final float mNextSample = mData[mNextIndex];
            final float mSample = mData[j];
            final float mInterpolatedSample = mSample * (1.0f - mFrac) + mNextSample * mFrac;
            return mInterpolatedSample * mAmplitude;
        } else {
            final float mSample = mData[j];
            return mSample * mAmplitude;
        }
    }

    public void rewind() {
        mArrayPtr = mDirectionForward ? mIn : mOut;
    }

    public void loop(boolean pLoop) {
        enable_loop(pLoop);
    }

    public void enable_loop(boolean pLoop) {
        mLoop = pLoop;
    }

    private int last_index() {
        return mData.length - 1;
    }

    private int wrapIndex(int i) {
        if (i > mOut) {
            i = mIn;
        } else if (i < mIn) {
            i = mOut;
        }
        return i;
    }
}
