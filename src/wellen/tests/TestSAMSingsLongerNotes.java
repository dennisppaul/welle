package wellen.tests;

import processing.core.PApplet;
import wellen.Beat;
import wellen.Note;
import wellen.SAM;
import wellen.Tone;
import wellen.dsp.DSP;
import wellen.dsp.Sampler;

public class TestSAMSingsLongerNotes extends PApplet {

    private static final float BORDER = 32;
    private float mLoopIn = 0.5f;
    private float mLoopOut = 0.9f;
    private SAM mSAM;
    private Sampler mSampler;
    private int mWordCounter = 0;
    private int mWordIndex = -1;
    private SingFragment[] mWords;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mSampler = new Sampler();
        mSampler.set_speed(0.5f);
        mSAM = new SAM();
        mSAM.set_sing_mode(true);

        mWords = new SingFragment[]{new SingFragment("EH", Note.NOTE_C3, 4, 0.50241286f, 0.8981233f),
                                    new SingFragment("VERIY", Note.NOTE_D3, 4, 0.85431075f, 0.9947749f),
                                    new SingFragment("TAYM", Note.NOTE_D3 + 1, 2, 0.2726049f, 0.43558058f),
                                    new SingFragment("AY", Note.NOTE_F3, 4, 0.07763485f, 0.3692169f),
                                    new SingFragment("SIYIY", Note.NOTE_G3, 4, 0.49991727f, 0.89988416f),
                                    new SingFragment("YUW", Note.NOTE_F3, 4, 0.5f, 0.9f),
                                    new SingFragment("FAO", Note.NOTE_D3 + 1, 4, 0.5917517f, 0.98083735f),
                                    new SingFragment("LIHNX", Note.NOTE_F3, 2, 0.4180811f, 0.61693764f),
                                    new SingFragment("", Note.NOTE_F3, 2, 0.5f, 0.9f),

                                    new SingFragment("AY", Note.NOTE_C3, 2, 0.07763485f, 0.3692169f),
                                    new SingFragment("GEHT", Note.NOTE_A3 + 1, 4, 0.5871952f, 0.64489114f),
                                    new SingFragment("DAWN", Note.NOTE_G3 + 1, 4, 0.6535342f, 0.76152956f),
                                    new SingFragment("AAN", Note.NOTE_G3, 2, 0.028384725f, 0.5691239f),
                                    new SingFragment("MAY", Note.NOTE_F3, 4, 0.36823878f, 0.59779024f),
                                    new SingFragment("NIYZ", Note.NOTE_D3 + 1, 4, 0.42345494f, 0.60453105f),
                                    new SingFragment("AEND", Note.NOTE_F3, 4, 0.10612828f, 0.34875825f),
                                    new SingFragment("PREY", Note.NOTE_C3, 4, 0.56590533f, 0.7307875f),
                                    new SingFragment("", Note.NOTE_C3, 6, 0.5f, 0.9f),};

        // | 1     |       | 2     |       | 3     |       | 4     |       |
        // | EH----------- | VERIY-------- | TAYM- | AY ---------- | SIY----

        // | 5     |       | 6     |       | 7     |       | 8     |       |
        // ------- |YUW----------- | FAO---------- | LIHNX-------- | AY--- |

        // | 9     |       | 10    |       | 11    |       | 12    |       |
        // | GEHT--------- | DAWN--------- | AAN-- | MAY---------- | NIYZ---

        // | 13    |       | 14    |       | 15    |       | 16    |       |
        // ------- | AEND--------- | PREY--------- |                       |

        DSP.start(this);
        Beat.start(this, 240 * 2);
    }

    public void draw() {
        background(255);
        translate(BORDER, BORDER);
        scale((width - BORDER * 2.0f) / width, (height - BORDER * 2.0f) / height);

        /* backdrop */
        noStroke();
        fill(0, 31);
        rect(0, 0, width, height);

        /* selection */
        noStroke();
        fill(191);
        float x0 = map(mSampler.get_loop_in(), 0, mSampler.get_buffer().length - 1, 0, width);
        float x1 = map(mSampler.get_loop_out(), 0, mSampler.get_buffer().length - 1, 0, width);
        if (mSampler.get_loop_in() >= 0 && mSampler.get_loop_out() >= 0) {
            if (mSampler.get_loop_in() < mSampler.get_loop_out()) {
                noStroke();
                beginShape();
                vertex(x0, 0);
                vertex(x1, 0);
                vertex(x1, height);
                vertex(x0, height);
                endShape();
            }
        }
        strokeWeight(4);
        stroke(0);
        line(x0, 0, x0, height);
        strokeWeight(1);
        stroke(0);
        line(x1, 0, x1, height);

        /* samples */
        noFill();
        stroke(0);
        beginShape();
        for (int i = 0; i < mSampler.get_buffer().length; i++) {
            float x = map(i, 0, mSampler.get_buffer().length, 0, width);
            float y = map(mSampler.get_buffer()[i], -1.0f, 1.0f, 0, height);
            vertex(x, y);
        }
        endShape();

        /* draw audio buffer */
        stroke(0, 63);
        DSP.draw_buffers(g, width, height);
    }

    public void keyPressed() {
        switch (key) {
            case '1':
                mLoopIn = map(mouseX, 0, width, 0, 1);
                mSampler.set_loop_in_normalized(mLoopIn);
                break;
            case '2':
                mLoopOut = map(mouseX, 0, width, 0, 1);
                mSampler.set_loop_out_normalized(mLoopOut);
                break;
        }
    }

    public void beat(int beatCount) {
        if (beatCount % 4 == 0) {
            Tone.note_on(Note.NOTE_C2, 60, 0.1f);
        } else if (beatCount % 4 == 2) {
            Tone.note_on(Note.NOTE_C3, 40, 0.1f);
        }

        if (mWordCounter == 0) {
            mWordIndex++;
            mWordIndex %= mWords.length;
            mWordCounter = mWords[mWordIndex].duration;
            if (mWords[mWordIndex].text.isEmpty()) {
                mSampler.stop();
            } else {
                mSAM.set_pitch(SAM.get_pitch_from_MIDI_note(mWords[mWordIndex].pitch));
                mSampler.set_buffer(mSAM.say(mWords[mWordIndex].text, true));
                mLoopIn = mWords[mWordIndex].loop_in;
                mLoopOut = mWords[mWordIndex].loop_out;
                mSampler.set_loop_in_normalized(mLoopIn);
                mSampler.set_loop_out_normalized(mLoopOut);
                mSampler.play();
            }
        } else if (mWordCounter == 1) {
            mSampler.stop();
        }
        mWordCounter--;
    }

    public void audioblock(float[] output_signal) {
        for (int i = 0; i < output_signal.length; i++) {
            output_signal[i] = mSampler.output() * 0.1f;
        }
    }

    static class SingFragment {
        final int duration;
        final float loop_in;
        final float loop_out;
        final int pitch;
        final String text;
        SingFragment(String pText, int pPitch, int pDuration, float pLoopIn, float pLoopOut) {
            text = pText;
            pitch = pPitch;
            duration = pDuration;
            loop_in = pLoopIn;
            loop_out = pLoopOut;
        }
    }

    public static void main(String[] args) {
        PApplet.main(TestSAMSingsLongerNotes.class.getName());
    }
}