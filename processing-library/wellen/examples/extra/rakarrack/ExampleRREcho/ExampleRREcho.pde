import wellen.*; 
import wellen.dsp.*; 

import wellen.extra.rakarrack.*;
ADSR mADSR;
final float mBaseFrequency = 4.0f * Wellen.DEFAULT_SAMPLING_RATE / Wellen.DEFAULT_AUDIOBLOCK_SIZE;
RREcho mEcho;
boolean mEnableEcho = true;
boolean mIsPlaying = false;
final float mMasterVolume = 0.75f;
final Wavetable mVCO = new Wavetable(512);
void settings() {
    size(640, 480);
}
void setup() {
    Wavetable.sine(mVCO.get_wavetable());
    mVCO.set_frequency(mBaseFrequency);
    mVCO.set_amplitude(0.5f);
    mADSR = new ADSR();
    mEcho = new RREcho();
    DSP.start(this, 2);
    Beat.start(this, 120 * 8);
}
void draw() {
    background(255);
    DSP.draw_buffers(g, width, height);
}
void mouseMoved() {
    mEcho.Tempo2Delay((int) map(mouseX, 0, width, 10, 300));
}
void beat(int beat) {
    if (random(1) > 0.8f) {
        if (mIsPlaying) {
            mADSR.stop();
        } else {
            mADSR.start();
            mVCO.set_frequency(mBaseFrequency * (int) random(1, 5));
        }
        mIsPlaying = !mIsPlaying;
    }
}
void keyPressed() {
    switch (key) {
        case 'q':
            Wavetable.fill(mVCO.get_wavetable(), Wellen.WAVEFORM_SINE);
            break;
        case 'w':
            Wavetable.fill(mVCO.get_wavetable(), Wellen.WAVEFORM_TRIANGLE);
            break;
        case 'e':
            Wavetable.fill(mVCO.get_wavetable(), Wellen.WAVEFORM_SAWTOOTH);
            break;
        case 'r':
            Wavetable.fill(mVCO.get_wavetable(), Wellen.WAVEFORM_SQUARE);
            break;
        case '1':
            mEcho.setpreset(RREcho.PRESET_ECHO_1);
            break;
        case '2':
            mEcho.setpreset(RREcho.PRESET_ECHO_2);
            break;
        case '3':
            mEcho.setpreset(RREcho.PRESET_ECHO_3);
            break;
        case '4':
            mEcho.setpreset(RREcho.PRESET_SIMPLE_ECHO);
            break;
        case '5':
            mEcho.setpreset(RREcho.PRESET_CANYON);
            break;
        case '6':
            mEcho.setpreset(RREcho.PRESET_PANNING_ECHO_1);
            break;
        case '7':
            mEcho.setpreset(RREcho.PRESET_PANNING_ECHO_2);
            break;
        case '8':
            mEcho.setpreset(RREcho.PRESET_PANNING_ECHO_3);
            break;
        case '9':
            mEcho.setpreset(RREcho.PRESET_FEEDBACK_ECHO);
            break;
        case '0':
            mEnableEcho = !mEnableEcho;
            break;
    }
}
void audioblock(float[] output_signalLeft, float[] output_signalRight) {
    for (int i = 0; i < output_signalLeft.length; i++) {
        output_signalLeft[i] = mVCO.output();
        final float mADSRValue = mADSR.output();
        output_signalLeft[i] *= mADSRValue;
        output_signalRight[i] = output_signalLeft[i];
    }
    if (mEnableEcho) {
        for (int i = 0; i < output_signalLeft.length; i++) {
            float mGain = 3.0f;
            output_signalLeft[i] *= mGain;
            output_signalRight[i] *= mGain;
        }
        mEcho.out(output_signalLeft, output_signalRight);
    }
    for (int i = 0; i < output_signalLeft.length; i++) {
        output_signalLeft[i] = Wellen.clamp(output_signalLeft[i]);
        output_signalLeft[i] *= mMasterVolume;
        output_signalRight[i] = Wellen.clamp(output_signalRight[i]);
        output_signalRight[i] *= mMasterVolume;
    }
}
