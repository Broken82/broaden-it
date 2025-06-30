import React, { createContext, useContext, useState } from 'react';

interface AudioContextProps {
  currentAudio: HTMLAudioElement | null;
  currentTrackId: string | null;
  play: (audio: HTMLAudioElement, trackId: string) => void;
  stop: () => void;
}

const AudioContext = createContext<AudioContextProps | undefined>(undefined);

export const useAudio = () => {
  const context = useContext(AudioContext);
  if (!context) {
    throw new Error('useAudio must be used within an AudioProvider');
  }
  return context;
};

export const AudioProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentAudio, setCurrentAudio] = useState<HTMLAudioElement | null>(null);
  const [currentTrackId, setCurrentTrackId] = useState<string | null>(null);

  const play = (audio: HTMLAudioElement, trackId: string) => {
    if (currentAudio) {
      currentAudio.pause();
      currentAudio.currentTime = 0; 
    }
    setCurrentAudio(audio);
    setCurrentTrackId(trackId); 
    audio.play();
  };

  const stop = () => {
    if (currentAudio) {
      currentAudio.pause();
      currentAudio.currentTime = 0;
      setCurrentAudio(null);
      setCurrentTrackId(null);
    }
  };

  return (
    <AudioContext.Provider value={{ currentAudio, currentTrackId, play, stop }}>
      {children}
    </AudioContext.Provider>
  );
};
