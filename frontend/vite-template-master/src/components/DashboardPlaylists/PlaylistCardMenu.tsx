import React, { useEffect, useRef, useState } from "react";
import { Menu, ActionIcon, Rating, Center } from "@mantine/core";
import { IconDotsVertical, IconPlayerPauseFilled, IconPlayerPlayFilled, IconPlus, IconTrash } from '@tabler/icons-react';
import { useAudio } from "@/contexts/AudioProvider";
import axios from "axios";

export function PlaylistCardMenu({onRemove, track }: {  onRemove: () => void, track: any }) {

  const audioRef = useRef<HTMLAudioElement | null>(null);
  const { currentTrackId, play, stop } = useAudio(); 
  const [rating, setRating] = useState<number | undefined>(undefined);

  const isPlaying = currentTrackId === track.id; 

  const handlePlayPreview = () => {
    if (!audioRef.current) {
      audioRef.current = new Audio(track.previewUrl);
      audioRef.current.volume = 0.25;
    }

    if (isPlaying) {
      stop(); 
    } else {
      play(audioRef.current, track.id); 
    }
  };

  async function handleRatingChange(value: any, track: any) {
    setRating(value);

    axios.post(`http://localhost:9191/rate/${track.id}`, 
    null,  
    {
        params: { rating: value }, 
        headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` }
    })
    .then((response) => {
        console.log(response);
    })
    .catch((error) => {
        console.error('Error rating track:', error);
    });
}

  async function getRating(track: any) {
    axios.get(`http://localhost:9191/rating/${track.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` }
    })
    .then((response) => {
      if (response.data !== rating) {
        setRating(response.data);  
      }
    })
    .catch((error) => {
        console.error('Error getting rating:', error);
    });
  }

  useEffect(() => {
    getRating(track);
  }, [track]);  


    return (
        <Menu.Dropdown>
          <Menu.Item leftSection={<IconTrash size={14} />} onClick={onRemove} color="red">
            Remove
          </Menu.Item>

          {track.previewUrl ? (
          <Menu.Item leftSection={isPlaying ? <IconPlayerPauseFilled size={14} /> : <IconPlayerPlayFilled size={14} />} onClick={handlePlayPreview}>
            {isPlaying ? "Stop Preview" : "Play Preview"}
          </Menu.Item>
        ) : (
          <Menu.Item disabled>Preview not available</Menu.Item>
        )}
          <Menu.Item>
            <Center>
            <Rating value={rating} onChange={(value) => handleRatingChange(value, track)} />
            </Center>
          </Menu.Item>
        </Menu.Dropdown>
    );
}
