import wellen.*; 

import wellen.extra.rakarrack.*;

final float mBaseFrequency = 4.0f * Wellen.DEFAULT_SAMPLING_RATE / Wellen.DEFAULT_AUDIOBLOCK_SIZE;

final float[] mCarrier = new float[Wellen.DEFAULT_AUDIOBLOCK_SIZE];

boolean mEnableVocoder = true;

final float mMasterVolume = 0.5f;

final Wavetable mVCO = new Wavetable(512);

RRVocoder mVocoder;

void settings() {
    size(640, 480);
}

void setup() {
    Wavetable.square(mVCO.get_wavetable());
    mVCO.set_frequency(55);
    mVCO.set_amplitude(0.75f);
    mVocoder = new RRVocoder(mCarrier, 32);
    DSP.start(this, 1, 1);
}

void draw() {
    background(255);
    DSP.draw_buffer(g, width, height);
}

void mouseMoved() {
    mVCO.set_frequency(map(mouseY, 0, height, 0.1f, 1000.0f));
}

void keyPressed() {
    switch (key) {
        case '1':
            mVocoder.setpreset(RRVocoder.PRESET_VOCODER_1);
            break;
        case '2':
            mVocoder.setpreset(RRVocoder.PRESET_VOCODER_2);
            break;
        case '3':
            mVocoder.setpreset(RRVocoder.PRESET_VOCODER_3);
            break;
        case '4':
            mVocoder.setpreset(RRVocoder.PRESET_VOCODER_4);
            break;
        case ' ':
            mEnableVocoder = !mEnableVocoder;
            break;
    }
}

void audioblock(float[] pOutputSignal, float[] pInputSignal) {
    RRUtilities.memcpy(pOutputSignal, pInputSignal, pInputSignal.length);
    for (int i = 0; i < mCarrier.length; i++) {
        mCarrier[i] = mVCO.output();
    }
    if (mEnableVocoder) {
        mVocoder.out(pOutputSignal, new float[Wellen.DEFAULT_AUDIOBLOCK_SIZE]);
    }
    for (int i = 0; i < pOutputSignal.length; i++) {
        if (mEnableVocoder) {
            pOutputSignal[i] *= 32;
        }
        pOutputSignal[i] = Wellen.clamp(pOutputSignal[i]);
        pOutputSignal[i] *= mMasterVolume;
    }
}