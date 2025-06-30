import React, { useEffect, useState } from 'react';
import { Modal, Grid, Card, Text, AspectRatio, Image, Menu } from '@mantine/core';
import classes from '../DashboardDiscover/NewReleases.module.css';
import { PlaylistCardMenu } from './PlaylistCardMenu';
import axios from 'axios';
import spotifyLogo from '../../svg/Full_Logo_Black_RGB.svg';

interface Track {
  id: number;
  title: string;
  artist: string;
  coverArtUrl: string;
}

export function PlaylistModal({ isOpen, onClose, playlistId }: { isOpen: boolean, onClose: () => void, playlistId: number }) {

  const [tracks, setTracks] = useState<Track[]>([]);


  useEffect(() => {
    if (isOpen && playlistId) {
  
      axios.get(`http://localhost:9191/playlists/${playlistId}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` }
      })
        .then((response) => {
          setTracks(response.data); 
        })
        .catch((error) => {
          console.error('Error: ', error); 
        })
    }
  }, [isOpen, playlistId]);

  function handleTrackDelete(trackId: number, playlistId: number){
    axios.delete(`http://localhost:9191/playlists/${playlistId}/delete/${trackId}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` }
    })
      .then((response) => {
        setTracks(tracks.filter((track) => track.id !== trackId));
      })
      .catch((error) => {
        console.error('Error: ', error);
      })

  }

  return (
    <Modal opened={isOpen} onClose={onClose} title="" centered size="lg" >
      <Grid justify="flex-start" gutter="xs" ml="xl" h={700} >
        {tracks.map((song) => (
          <Grid.Col
            span={4}
            key={song.id}
            style={{
              flexBasis: 'calc(33.3333% - 16px)'
            }}
          >
            <img src={spotifyLogo} alt="Vinyl Logo" width={70}  />
            <Menu>
              <Menu.Target>
            <Card shadow='sm' padding="sm" className={classes.card} withBorder>
              <AspectRatio ratio={1}>
                <Image
                  w="105px"
                  src={song.coverArtUrl}
                  radius="4px"
                  alt={song.title}
                />
              </AspectRatio>
              <Text className={classes.title} lineClamp={1}>{song.title}</Text>
              <Text className={classes.artist} size="sm" lineClamp={1}>{song.artist}</Text>
            </Card>
            </Menu.Target>
            <PlaylistCardMenu track={song} onRemove={() => {handleTrackDelete(song.id, playlistId)}} />
            </Menu>
          </Grid.Col>
        ))}
      </Grid>
    </Modal>
  );
}