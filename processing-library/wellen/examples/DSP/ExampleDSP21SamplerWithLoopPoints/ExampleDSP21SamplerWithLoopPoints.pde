import wellen.*; 
import wellen.dsp.*; 

/*
 * this example demonstrates how to use a sampler with loop in- and out-points. the loop-points specify a region
 * in the sample buffer that loops once the sample playback is started with `start()` until `stop()` is called.
 * after this call the remaining region between loop out-point and the rest of the sample buffer is processed.
 *
 * note that *global* in- and out-points can be defined with `set_in()` + `set_out()`.
 */

static final float BORDER = 32;

Sampler fSampler;

void settings() {
    size(640, 480);
}

void setup() {
    byte[] mData = loadBytes("../../../resources/hello.raw");
    fSampler = new Sampler();
    fSampler.load(mData);
    fSampler.forward();
    fSampler.set_loop_in_normalized(0.1579227f);
    fSampler.set_loop_out_normalized(0.23951691f);
    fSampler.enable_loop(true);
    DSP.start(this);
}

void draw() {
    background(255);
    translate(BORDER, BORDER);
    scale((width - BORDER * 2.0f) / width, (height - BORDER * 2.0f) / height);
    /* backdrop */
    noStroke();
    fill(0, 31);
    rect(0, 0, width, height);
    /* selection */
    fill(0, 31);
    float x0 = map(fSampler.get_loop_in(), 0, fSampler.get_buffer().length, 0, width);
    float x1 = map(fSampler.get_loop_out(), 0, fSampler.get_buffer().length, 0, width);
    if (fSampler.get_loop_in() >= 0 && fSampler.get_loop_out() >= 0) {
        if (fSampler.get_loop_in() < fSampler.get_loop_out()) {
            noStroke();
            beginShape();
            vertex(x0, 0);
            vertex(x1, 0);
            vertex(x1, height);
            vertex(x0, height);
            endShape();
        }
    }
    stroke(0);
    strokeWeight(4);
    line(x0, 0, x0, height);
    strokeWeight(1);
    line(x1, 0, x1, height);
    /* samples */
    noFill();
    stroke(0);
    beginShape();
    for (int i = 0; i < fSampler.get_buffer().length; i++) {
        float x = map(i, 0, fSampler.get_buffer().length, 0, width);
        float y = map(fSampler.get_buffer()[i], -1.0f, 1.0f, 0, height);
        vertex(x, y);
    }
    endShape();
    /* draw audio buffer */
    stroke(0, 63);
    noFill();
    DSP.draw_buffers(g, width, height);
}

void mousePressed() {
    fSampler.play();
    fSampler.rewind();
    fSampler.enable_loop(true);
}

void mouseReleased() {
    fSampler.enable_loop(false);
}

void keyPressed() {
    switch (key) {
        case '1':
            fSampler.set_loop_in_normalized(map(mouseX, BORDER, width - BORDER, 0, 1));
            break;
        case '2':
            fSampler.set_loop_out_normalized(map(mouseX, BORDER, width - BORDER, 0, 1));
            break;
        case 'z':
            int[] mLoopPoints = Wellen.find_zero_crossings(fSampler.get_buffer(),
                                                           fSampler.get_loop_in(),
                                                           fSampler.get_loop_out());
            if (mLoopPoints[0] > 0 && mLoopPoints[1] > 0) {
                fSampler.set_loop_in(mLoopPoints[0]);
                fSampler.set_loop_out(mLoopPoints[1]);
            }
            break;
        case ' ':
            fSampler.set_loop_in(Sampler.NO_LOOP_POINT);
            fSampler.set_loop_out(Sampler.NO_LOOP_POINT);
            break;
        case 'd':
            fSampler.set_speed(fSampler.get_speed() * -1);
            break;
    }
}

void audioblock(float[] output_signal) {
    for (int i = 0; i < output_signal.length; i++) {
        output_signal[i] = fSampler.output();
    }
}
