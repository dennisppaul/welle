import wellen.*; 
import wellen.dsp.*; 

/*
 * this example demonstrates how to use envelopes to change values over time.
 *
 * the left circle shows the amplitude controlled by an envelope, while the right circle visualizes the step size of
 * the simplex noise generator.
 */
Envelope mEnvelopeAmplitude;
Envelope mEnvelopeStepSize;
Noise mNoise;
void settings() {
    size(640, 480);
}
void setup() {
    mEnvelopeAmplitude = new Envelope();
    mEnvelopeAmplitude.add_stage(0.0f, 2.0f);
    mEnvelopeAmplitude.add_stage(1.0f, 5.0f);
    mEnvelopeAmplitude.add_stage(0.0f);
    mEnvelopeStepSize = new Envelope();
    mEnvelopeStepSize.add_stage(0.001f, 7.0f);
    mEnvelopeStepSize.add_stage(0.02f);
    mNoise = new Noise();
    mNoise.set_amplitude(0.25f);
    mNoise.set_type(Wellen.NOISE_SIMPLEX);
    DSP.start(this);
}
void draw() {
    background(255);
    DSP.draw_buffers(g, width, height);
    fill(0);
    ellipse(width * 0.33f,
            map(mEnvelopeAmplitude.get_current_value(), 0.0f, 1.0f, height * 0.1f, height * 0.9f),
            20,
            20);
    ellipse(width * 0.66f,
            map(mEnvelopeStepSize.get_current_value(), 0.001f, 0.02f, height * 0.1f, height * 0.9f),
            40,
            40);
}
void mousePressed() {
    mEnvelopeStepSize.start();
    mEnvelopeAmplitude.start();
}
void audioblock(float[] output_signal) {
    for (int i = 0; i < output_signal.length; i++) {
        output_signal[i] = mEnvelopeAmplitude.output() * mNoise.output();
        mEnvelopeStepSize.output(); // we just need to update the envelope
        mNoise.set_step(mEnvelopeStepSize.get_current_value());
    }
}
