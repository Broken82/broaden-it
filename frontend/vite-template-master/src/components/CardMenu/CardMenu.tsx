import React, { useEffect, useRef } from "react";
import { Menu } from "@mantine/core";
import { IconPlus, IconTrash, IconPlayerPlayFilled, IconPlayerPauseFilled } from '@tabler/icons-react';
import { useAudio } from "@/contexts/AudioProvider"; 
import { useDisclosure } from "@mantine/hooks";
import { CardMenuPlaylistModal } from "./CardMenuPlaylistModal";
import { useLocation } from "react-router-dom";

export function CardMenu({ track, artist }: { track: any; artist: string }) {
  const [opened, { open, close }] = useDisclosure(false);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const { currentTrackId, play, stop } = useAudio(); 
  const location = useLocation();

  const isPlaying = currentTrackId === track.id; 

  const handlePlayPreview = () => {
    if (!audioRef.current) {
      audioRef.current = new Audio(track.preview_url);
      audioRef.current.volume = 0.25;
    }

    if (isPlaying) {
      stop(); 
    } else {
      play(audioRef.current, track.id); 
    }
  };



  return (
    <>
      <Menu.Dropdown>
        <Menu.Item leftSection={<IconPlus size={14} />} onClick={open}>
          Add to Playlist
        </Menu.Item>
        {track.preview_url ? (
          <Menu.Item leftSection={isPlaying ? <IconPlayerPauseFilled size={14} /> : <IconPlayerPlayFilled size={14} />} onClick={handlePlayPreview}>
            {isPlaying ? "Stop Preview" : "Play Preview"}
          </Menu.Item>
        ) : (
          <Menu.Item disabled>Preview not available</Menu.Item>
        )}
      </Menu.Dropdown>
      <CardMenuPlaylistModal isOpen={opened} onClose={close} track={track} artist={artist} />
    </>
  );
}
