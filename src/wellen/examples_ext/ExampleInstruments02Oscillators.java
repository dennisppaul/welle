package wellen.examples_ext;

import processing.core.PApplet;
import wellen.Note;
import wellen.Scale;
import wellen.Tone;
import wellen.Wellen;

/**
 * this example shows how to use different oscillators in an instrument.
 * <p>
 * note that this functionality is not implemented for MIDI and OSC.
 */
public class ExampleInstruments02Oscillators extends PApplet {

    private int mNote;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        background(255);
    }

    public void draw() {
        background(255);
        fill(0);
        float mSize = map(mNote, 33, 69, 80, 320);
        ellipse(width * 0.5f, height * 0.5f, Tone.is_playing() ? mSize : 5, Tone.is_playing() ? mSize : 5);
    }

    public void mousePressed() {
        mNote = Scale.get_note(Scale.MAJOR_CHORD_7, Note.NOTE_A2, (int) random(0, 10));
        Tone.note_on(mNote, 127);
    }

    public void mouseReleased() {
        Tone.note_off();
    }

    public void keyPressed() {
        if (key == '1') {
            Tone.instrument().set_oscillator_type(Wellen.OSC_SINE);
        }
        if (key == '2') {
            Tone.instrument().set_oscillator_type(Wellen.OSC_TRIANGLE);
        }
        if (key == '3') {
            Tone.instrument().set_oscillator_type(Wellen.OSC_SAWTOOTH);
        }
        if (key == '4') {
            Tone.instrument().set_oscillator_type(Wellen.OSC_SQUARE);
        }
        if (key == '5') {
            Tone.instrument().set_oscillator_type(Wellen.OSC_NOISE);
        }
    }

    public static void main(String[] args) {
        PApplet.main(ExampleInstruments02Oscillators.class.getName());
    }
}
