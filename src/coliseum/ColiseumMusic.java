package coliseum;

import flounder.devices.*;
import flounder.events.*;
import flounder.inputs.*;
import flounder.noise.*;
import flounder.standard.*;
import org.lwjgl.glfw.*;

import javax.sound.midi.*;
import java.util.*;

public class ColiseumMusic extends IStandard {
	public VocalsPlayer player;

	public Random random;

	private double volume;
	private double tempo;
	private int root, mood, measure;
	private int msTime, tick, totalTick;
	private boolean changingNote, resetMeasure;
	private long startBeatTime;

	private PerlinNoise pnPn, pnPd, pnBn, pnBd;

	private int[] ns, os;
	private int pTime, bTime;
	private int intMax, intMin;
	private int pIntensity, bIntensity;


	public ColiseumMusic() {
		super(FlounderSound.class);
	}

	@Override
	public void init() {
		this.player = new VocalsPlayer();

		this.random = new Random(ColiseumSeed.getSeed());

		this.volume = 1.0f;
		this.tempo = random.nextInt(120) + 50;
		this.root = random.nextInt(30) + 55;
		this.mood = random.nextInt(3);
		this.measure = 1;
		this.msTime = (int) (90000.0 / tempo / 8.0);
		this.tick = 0;
		this.totalTick = 0;
		this.changingNote = true;
		this.resetMeasure = true;
		this.startBeatTime = System.currentTimeMillis();

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton button = new KeyButton(GLFW.GLFW_KEY_R);

			@Override
			public boolean eventTriggered() {
				return button.wasDown();
			}

			@Override
			public void onEvent() {
				ColiseumSeed.randomize();
				changeSeed();
			}
		});

		changeSeed();
	}

	@Override
	public void update() {
		if (resetMeasure) {
			random = new Random(ColiseumSeed.getSeed() % measure + measure % ColiseumSeed.getSeed() + ColiseumSeed.getSeed() / measure);
			int rMood = random.nextInt(5) + mood;
			int t = rMood % 7 * 2;
			int n1 = t - ((t > 4) ? 1 : 0) + ((t < -4) ? 1 : 0) + root;
			int o1 = rMood / 7;
			rMood += 2;
			t = rMood % 7 * 2;
			int n2 = t - ((t > 4) ? 1 : 0) + ((t < -4) ? 1 : 0) + root;
			int o2 = rMood / 7;
			rMood += 2;
			t = rMood % 7 * 2;
			int n3 = t - ((t > 4) ? 1 : 0) + ((t < -4) ? 1 : 0) + root;
			int o3 = rMood / 7;

			resetMeasure = false;

			ns = new int[]{n1, n2, n3};
			os = new int[]{o1, o2, o3};

			pTime = 0;
			bTime = 0;
			intMax = 5;
			intMin = 1;
			pIntensity = random.nextInt(intMax - intMin) + intMin;
			bIntensity = random.nextInt(intMax - intMin - 2) + intMin + 1;
		}

		if (tick < 32) {
			if (msTime < System.currentTimeMillis() - startBeatTime) {
				pTime--;
				bTime--;

				tick++;
				totalTick++;
				changingNote = true;
				startBeatTime = System.currentTimeMillis();
			}

			if (changingNote) {
				/**
				 * Violin (channel 1). Violin uses generated notes to make sounds.
				 */
				{
					if (tick % 2 == 0) {
						player.startNote(ns[0] + os[0] * 12, (int) (40 * volume), 1, totalTick);
					}

					if (tick % 10 == 0) {
						player.startNote(ns[1] + os[1] * 12, (int) (40 * volume), 1, totalTick);
					}

					if (tick % 18 == 0) {
						player.startNote(ns[2] + os[2] * 12, (int) (40 * volume), 1, totalTick);
					}
				}

				/**
				 * Percussion (channel 9). Random: (28, 29, 30, 32).<br>
				 * Electro: (60, 61, 67, 68, 69, 70, 73, 75).<br>
				 * Zap (27).<br>
				 * Tom tom (41, 43, 45, 47, 48, 50).<br>
				 * Bass Drum (35, 36).<br>
				 * Stick Tap (31, 33, 37, 62).<br>
				 * Bell (34).<br>
				 * Snare (38, 40).<br>
				 * Clappers (39).<br>
				 * Cymbol (42, 44).<br>
				 * High hat (46, 49, 51, 53, 55, 57, 59).<br>
				 * Gong (52).<br>
				 * Systrum (54).<br>
				 * Cowbells (56).<br>
				 * Cowboy Spinners (58).<br>
				 * Bongo (63, 64, 65, 66).<br>
				 * Whistle (71, 72).<br>
				 * Washboard (74).<br>
				 */
				{
					if ((tick + 8) % 16 == 0) { // Snare (38, 40)
						player.startNote(38, (int) (80 * volume), 9, totalTick);
					}

					if (tick % 4 == 0) { // Bass (35, 36)
						player.startNote(35, (int) (85 * volume), 9, totalTick);
					}

					if (tick + 2 % 6 == 0 && tick % 8 != 0) { // Cymbol (42, 44)
						player.startNote(42, (int) (80 * volume), 9, totalTick);
					}
				}

				/**
				 * Piano 1 (channel 0). Piano uses generated notes to make sounds.
				 */
				int pTick = tick;
				pTick += (pTick == 31) ? mood * 0.25 : 0;

				if (pTick % (1.25 * mood) == 0) {
					pTime = (int) Math.pow(2.0, random.nextInt(pIntensity) + intMin);

					if (tick + pTime > 32) {
						pTime = 32 - tick;
					}

					int vel = (int) (pnPd.noise1(tick / 32.0f + measure) * 60.0f) + 80;
					int rnd = (int) ((pnPn.noise1(tick / 32.0f + measure) + 0.5) * 20.0);
					int pNote = ns[rnd % 3] + ((os[rnd % 3] - 1) * 12);
					player.startNote(pNote, (int) (vel * volume), 0, totalTick);
				}

				/**
				 * Guitar (channel 2). Guitar uses generated notes to make sounds.
				 */
				if (tick % (0.75 / mood) == 0) {
					bTime = (int) Math.pow(2.0, random.nextInt(bIntensity) + intMin);

					if (tick + bTime > 32) {
						bTime = 32 - tick;
					}

					int vel = (int) (pnBd.noise1(tick / 32.0f + measure) * 60.0f) + 100;
					int rnd = (int) ((pnBn.noise1(tick / 32.0f + measure) + 0.5) * 4.0);
					int bNote = ns[0] + (os[0] - 4 + rnd) * 12;
					player.startNote(bNote, (int) (vel * volume), 2, totalTick);
				}

				changingNote = false;
			}
		} else {
			player.stopAllNotes();
			measure++;
			tick = 1;
			resetMeasure = true;
			startBeatTime = System.currentTimeMillis();
		}
	}

	/**
	 * Change the perlin seed being used to create music.
	 */
	public void changeSeed() {
		player.stopAllNotes();
		random = new Random(ColiseumSeed.getSeed());
		tempo = random.nextInt(120) + 50;
		player.clearTracks();

		int usTempo = (int) (6.0e7 / tempo);
		byte[] tempoB = {(byte) (usTempo >> 16), (byte) ((usTempo & 0xFF00) >> 8), (byte) (usTempo & 0xFF)};

		try {
			player.tracks[0].add(new MidiEvent(new MetaMessage(81, tempoB, 3), 0L));
		} catch (Exception e) {
			e.printStackTrace();
		}

		root = random.nextInt(64) + 31;
		mood = random.nextInt(3);
		measure = 1;
		msTime = (int) (90000.0 / tempo / 8.0);
		tick = 1;
		totalTick = 1;
		changingNote = true;
		resetMeasure = true;

		pnPn = new PerlinNoise((int) (ColiseumSeed.getSeed() * 2L + 1L));
		pnPd = new PerlinNoise((int) (ColiseumSeed.getSeed() * 3L + 2L));
		pnBn = new PerlinNoise((int) (ColiseumSeed.getSeed() * 4L + 3L));
		pnBd = new PerlinNoise((int) (ColiseumSeed.getSeed() * 5L + 4L));

		player.setInstrument(48, 1, 0);
		player.setInstrument(33, 2, 0);
	}

	@Override
	public void profile() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isActive() {
		return true;
	}

	public class VocalsPlayer {
		private Synthesizer synth;
		private MidiChannel[] channel;
		private Instrument[] instrument;
		private int insts;
		public Sequence seq;
		public Track[] tracks;

		/**
		 * Creates a new music player.
		 */
		public VocalsPlayer() {
			this.insts = 0;
			this.tracks = new Track[16];

			try {
				(this.synth = MidiSystem.getSynthesizer()).open();
				this.channel = this.synth.getChannels();
				this.instrument = this.synth.getAvailableInstruments();
				this.synth.loadInstrument(this.instrument[this.insts]);

				for (int x = 0; x < this.channel.length; x++) {
					this.channel[x].programChange(this.insts);
				}

				this.seq = new Sequence(0.0f, 8);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (int x = 0; x < 16; ++x) {
				this.tracks[x] = this.seq.createTrack();
			}
		}

		/**
		 * Plays a note for a set time.
		 *
		 * @param note The note to be played.
		 * @param volume The volume of the note.
		 * @param channel What channel it should play on.
		 * @param length Length of the played note.
		 * @param time How long it should play.
		 */
		public void playNote(int note, int volume, int channel, int length, int time) {
			this.channel[channel].noteOn(note, volume);

			try {
				Thread.sleep(length);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.channel[channel].noteOff(note);
		}

		/**
		 * Starts a new note.
		 *
		 * @param note The note to be played.
		 * @param volume The volume of the note.
		 * @param channel What channel it should play on.
		 * @param time How long it should play.
		 */
		public void startNote(int note, int volume, int channel, int time) {
			this.channel[channel].noteOn(note, volume);

			try {
				this.tracks[channel].add(new MidiEvent(new ShortMessage(144, channel, note, volume), time));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		/**
		 * Stops a existing note.
		 *
		 * @param note The note to be played.
		 * @param channel What channel to use.
		 * @param time How long it should play.
		 */
		public void stopNote(int note, int channel, int time) {
			this.channel[channel].noteOff(note);

			try {
				this.tracks[channel].add(new MidiEvent(new ShortMessage(128, channel, note, 0), time));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		/**
		 * Stops all notes.
		 */
		public void stopAllNotes() {
			for (final MidiChannel c : this.channel) {
				c.allNotesOff();
			}
		}

		/**
		 * Sets a program and channel for a new instrument.
		 *
		 * @param program ID of the new program.
		 * @param channel What channel to use.
		 * @param time How long it should play.
		 * <p>
		 * Horn (63, 109, 110)<br>
		 * Flute (54, 72, 77, 78)<br>
		 * Violin (48, 89)<br>
		 * Piano (0, 1, 2, 3)<br>
		 * Organ (16, 19, 20, 85, 91)<br>
		 * Xylophone (8, 9, 10, 11, 12) <br>
		 * Bells (14)
		 */
		public void setInstrument(int program, int channel, int time) {
			this.channel[channel].programChange(program);

			try {
				this.tracks[channel].add(new MidiEvent(new ShortMessage(192, channel, program, 0), time));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		/**
		 * Clears all tracks to a empty state.
		 */
		public void clearTracks() {
			for (int x = 0; x < 16; ++x) {
				this.seq.deleteTrack(this.tracks[x]);
				this.tracks[x] = this.seq.createTrack();
			}
		}
	}
}
