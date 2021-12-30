package wellen.tests.rakarrack;

import processing.core.PApplet;
import wellen.ADSR;
import wellen.Beat;
import wellen.DSP;
import wellen.Wavetable;
import wellen.Wellen;

import static wellen.Wellen.DEFAULT_AUDIOBLOCK_SIZE;
import static wellen.Wellen.clamp;
import static wellen.tests.rakarrack.RREchotron.PRESET_AMBIENCE;
import static wellen.tests.rakarrack.RREchotron.PRESET_ARRANJER;
import static wellen.tests.rakarrack.RREchotron.PRESET_SUCFLANGE;
import static wellen.tests.rakarrack.RREchotron.PRESET_SUCTION;
import static wellen.tests.rakarrack.RREchotron.PRESET_SUMMER;

public class TestRREchotron extends PApplet {

    private ADSR mADSR;
    private final float mBaseFrequency = 2.0f * Wellen.DEFAULT_SAMPLING_RATE / DEFAULT_AUDIOBLOCK_SIZE;
    private RREchotron mEchotron;
    private boolean mEnableEchotron = true;
    private boolean mIsPlaying = false;
    private final float mMasterVolume = 0.8f;
    private final Wavetable mVCO = new Wavetable(512);

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        Wavetable.sawtooth(mVCO.get_wavetable());
        mVCO.set_frequency(mBaseFrequency);
        mVCO.set_amplitude(0.5f);
        mADSR = new ADSR();
        mEchotron = new RREchotron();

        DSP.start(this, 2);
        Beat.start(this, 120 * 4);
    }

    public void draw() {
        background(255);
        DSP.draw_buffer_stereo(g, width, height);
    }

    public void beat(int pBeat) {
        if (random(1) > 0.4f) {
            if (mIsPlaying) {
                mADSR.stop();
            } else {
                mADSR.start();
                final float mFifth = (random(1) > 0.4f) ? 1 : 2.0f/3.0f;
                mVCO.set_frequency(mBaseFrequency * (int) random(1, 5) / mFifth);
            }
            mIsPlaying = !mIsPlaying;
        }
    }

    public void keyPressed() {
        switch (key) {
            case 'q':
                Wavetable.fill(mVCO.get_wavetable(), Wellen.WAVESHAPE_SINE);
                break;
            case 'w':
                Wavetable.fill(mVCO.get_wavetable(), Wellen.WAVESHAPE_TRIANGLE);
                break;
            case 'e':
                Wavetable.fill(mVCO.get_wavetable(), Wellen.WAVESHAPE_SAWTOOTH);
                break;
            case 'r':
                Wavetable.fill(mVCO.get_wavetable(), Wellen.WAVESHAPE_SQUARE);
                break;
            case '1':
                mEchotron.setpreset(PRESET_SUMMER);
                break;
            case '2':
                mEchotron.setpreset(PRESET_AMBIENCE);
                break;
            case '3':
                mEchotron.setpreset(PRESET_ARRANJER);
                break;
            case '4':
                mEchotron.setpreset(PRESET_SUCTION);
                break;
            case '5':
                mEchotron.setpreset(PRESET_SUCFLANGE);
                break;
            case '0':
                mEnableEchotron = !mEnableEchotron;
                break;
        }
    }

    public void audioblock(float[] pOutputSignalLeft, float[] pOutputSignalRight) {
        for (int i = 0; i < pOutputSignalLeft.length; i++) {
            pOutputSignalLeft[i] = mVCO.output();
            final float mADSRValue = mADSR.output();
            pOutputSignalLeft[i] *= mADSRValue;
            pOutputSignalRight[i] = pOutputSignalLeft[i];
        }
        if (mEnableEchotron) {
            mEchotron.out(pOutputSignalLeft, pOutputSignalRight);
        }
        for (int i = 0; i < pOutputSignalLeft.length; i++) {
            pOutputSignalLeft[i] = clamp(pOutputSignalLeft[i]);
            pOutputSignalLeft[i] *= mMasterVolume;
            pOutputSignalRight[i] = clamp(pOutputSignalRight[i]);
            pOutputSignalRight[i] *= mMasterVolume;
        }
    }

    public static void main(String[] args) {
        PApplet.main(TestRREchotron.class.getName());
    }
}
