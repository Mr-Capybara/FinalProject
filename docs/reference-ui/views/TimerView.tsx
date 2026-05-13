import { LayoutList, Play, Pause, Square, Music } from 'lucide-react';
import { useState, useEffect, useRef } from 'react';

export function TimerView() {
  const [isPlaying, setIsPlaying] = useState(false);
  const [timeLeft, setTimeLeft] = useState(25 * 60);

  const [isPlayingNoise, setIsPlayingNoise] = useState(false);
  const audioCtxRef = useRef<AudioContext | null>(null);
  const noiseNodeRef = useRef<AudioBufferSourceNode | null>(null);
  const gainNodeRef = useRef<GainNode | null>(null);

  useEffect(() => {
    return () => {
      if (noiseNodeRef.current) {
        try { noiseNodeRef.current.stop(); } catch(e) {}
      }
      if (audioCtxRef.current) {
        try { audioCtxRef.current.close(); } catch(e) {}
      }
    };
  }, []);

  useEffect(() => {
    let timer: number;
    if (isPlaying && timeLeft > 0) {
      timer = window.setInterval(() => {
        setTimeLeft(prev => prev - 1);
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [isPlaying, timeLeft]);

  const togglePlay = () => setIsPlaying(!isPlaying);
  const resetTimer = () => {
    setIsPlaying(false);
    setTimeLeft(25 * 60);
  };
  
  const toggleNoise = () => {
    if (isPlayingNoise) {
      // Fade out and stop
      if (gainNodeRef.current && audioCtxRef.current) {
        gainNodeRef.current.gain.setTargetAtTime(0, audioCtxRef.current.currentTime, 0.5);
        setTimeout(() => {
          if (noiseNodeRef.current) {
            noiseNodeRef.current.stop();
            noiseNodeRef.current.disconnect();
            noiseNodeRef.current = null;
          }
        }, 500);
      }
      setIsPlayingNoise(false);
    } else {
      // Start brown noise
      if (!audioCtxRef.current) {
         audioCtxRef.current = new (window.AudioContext || (window as any).webkitAudioContext)();
      }
      const ctx = audioCtxRef.current;
      if (ctx.state === 'suspended') {
        ctx.resume();
      }
      
      const bufferSize = ctx.sampleRate * 2; // 2 seconds buffer
      const buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate);
      const data = buffer.getChannelData(0);
      let lastOut = 0;
      for (let i = 0; i < bufferSize; i++) {
        const white = Math.random() * 2 - 1;
        data[i] = (lastOut + (0.02 * white)) / 1.02;
        lastOut = data[i];
        data[i] *= 3.5; // (compensate gain)
      }
      
      const noise = ctx.createBufferSource();
      noise.buffer = buffer;
      noise.loop = true;
      
      const gain = ctx.createGain();
      gain.gain.value = 0; // start at 0
      gain.gain.setTargetAtTime(0.5, ctx.currentTime, 1); // fade in to 0.5
      
      const filter = ctx.createBiquadFilter();
      filter.type = 'lowpass';
      filter.frequency.value = 400; // Deep comforting rumble
      
      noise.connect(filter);
      filter.connect(gain);
      gain.connect(ctx.destination);
      
      noise.start();
      
      noiseNodeRef.current = noise;
      gainNodeRef.current = gain;
      setIsPlayingNoise(true);
    }
  };

  const minutes = Math.floor(timeLeft / 60).toString().padStart(2, '0');
  const seconds = (timeLeft % 60).toString().padStart(2, '0');

  const progress = ((25 * 60 - timeLeft) / (25 * 60)) * 301.59;

  return (
    <div className="flex-grow flex flex-col items-center justify-center pb-[100px] pt-8 md:pb-container-padding md:pt-12 w-full max-w-lg mx-auto relative z-10 h-full min-h-[550px]">
      {/* Current Task Context */}
      <div className="mb-8 md:mb-12 w-full flex flex-col items-center text-center space-y-4 relative z-20">
        <span className="inline-flex items-center gap-2 bg-secondary-container/30 text-on-secondary-container font-label-sm text-label-sm px-4 py-1.5 rounded-full uppercase tracking-wider">
          <LayoutList size={16} />
          Focusing on
        </span>
        <h2 className="font-headline-md text-headline-md text-on-surface px-4">Redesign Onboarding Flow</h2>
        <p className="font-body-md text-body-md text-on-surface-variant">Session 2 of 4</p>
      </div>

      {/* The Timer Dial */}
      <div className="relative w-64 h-64 md:w-80 md:h-80 flex items-center justify-center mb-16 shrink-0 group">
        <div className="absolute inset-0 bg-primary-container/20 rounded-full blur-3xl opacity-50 group-hover:opacity-70 transition-opacity duration-1000"></div>
        
        <svg className="absolute inset-0 w-full h-full -rotate-90 pointer-events-none" viewBox="0 0 100 100">
          <circle className="stroke-surface-container-high" cx="50" cy="50" fill="none" r="48" strokeWidth="2" />
          <circle 
            className="stroke-primary" 
            cx="50" cy="50" fill="none" r="48" 
            strokeDasharray="301.59" 
            strokeDashoffset={-progress} 
            strokeLinecap="round" strokeWidth="4" 
            style={{ transition: 'stroke-dashoffset 1s linear' }}
          />
        </svg>

        <div className="absolute inset-2 bg-surface rounded-full shadow-[0px_8px_32px_rgba(0,0,0,0.06)] flex flex-col items-center justify-center z-10">
          <div className="font-headline-lg text-[64px] md:text-[80px] font-light text-primary leading-none tracking-tighter mb-2 font-mono">
            {minutes}:{seconds}
          </div>
        </div>
      </div>

      {/* Controls */}
      <div className="flex items-center justify-center gap-stack-gap w-full">
        <button onClick={resetTimer} className="w-14 h-14 rounded-2xl bg-surface shadow-[0px_4px_20px_rgba(0,0,0,0.04)] text-on-surface-variant flex items-center justify-center hover:bg-surface-container-high hover:text-on-surface transition-all active:scale-95 group focus:outline-none focus:ring-2 focus:ring-primary-container">
          <Square size={24} className="group-hover:text-error transition-colors" />
        </button>
        <button onClick={togglePlay} className="w-20 h-20 rounded-2xl bg-primary shadow-lg shadow-primary/20 text-on-primary flex items-center justify-center hover:opacity-90 transition-all active:scale-95 focus:outline-none focus:ring-4 focus:ring-primary-container">
          {isPlaying ? <Pause size={40} fill="currentColor" /> : <Play size={40} fill="currentColor" />}
        </button>
        <button onClick={toggleNoise} className={`w-14 h-14 rounded-2xl shadow-[0px_4px_20px_rgba(0,0,0,0.04)] flex items-center justify-center transition-all active:scale-95 focus:outline-none focus:ring-2 focus:ring-primary-container ${isPlayingNoise ? 'bg-secondary-container text-on-secondary-container' : 'bg-surface text-on-surface-variant hover:bg-surface-container-high hover:text-on-surface'}`}>
          <Music size={24} />
        </button>
      </div>
    </div>
  );
}
