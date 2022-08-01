package wellen.tests.daisysp;

import processing.core.PApplet;
import wellen.Beat;
import wellen.DSP;
import wellen.Wellen;
import wellen.extern.daisysp.Drip;

public class TestDaisySPDrip extends PApplet {

    private Drip mPluck;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mPluck = new Drip();
        mPluck.Init(Wellen.DEFAULT_SAMPLING_RATE, 0.1f);
        DSP.start(this);
        Beat.start(this, 60);
    }

    public void draw() {
        background(255);
        noStroke();
        fill(0);
        float mScale = 0.98f * height;
        circle(width * 0.5f, height * 0.5f, mScale);
        stroke(255);
        DSP.draw_buffer(g, width, height);
    }

    public void beat(int pBeatCount) {
        mPluck.Trig();
    }

    public void audioblock(float[] pOutputSignal) {
        for (int i = 0; i < pOutputSignal.length; i++) {
            pOutputSignal[i] = mPluck.Process();
        }
    }

    public static void main(String[] args) {
        PApplet.main(TestDaisySPDrip.class.getName());
    }
}