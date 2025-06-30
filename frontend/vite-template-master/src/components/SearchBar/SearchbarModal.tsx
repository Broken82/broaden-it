import React, { useState } from 'react';
import { Modal, Grid, TextInput, Card, Text, AspectRatio, Image, Loader, Button, Menu, rem, Container } from '@mantine/core';
import axios from 'axios';
import classes from '../DashboardDiscover/NewReleases.module.css';
import { CardMenu } from '../CardMenu/CardMenu';
import { IconPlus, IconSearch } from '@tabler/icons-react';
import spotifyLogo from '../../svg/Full_Logo_Black_RGB.svg';
import { c } from 'vite/dist/node/types.d-aGj9QkWt';

interface Track {
  image: string
  artists: string
  href: string;
  name: string;
  preview_url: string;
}

interface Response {
  tracks: Track[];
}

export function SearchModal({ isOpen, onClose, onAlbumSelect, source }: { isOpen: boolean, onClose: () => void, onAlbumSelect?: (albumUrl: string) => void, source?: string }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [results, setResults] = useState<Track[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const handleCardClick = (albumUrl: string) => {
    if (onAlbumSelect) {
      onAlbumSelect(albumUrl); 
      onClose(); 
    }
  };

  const mapSpotifyDataToTracks = (spotifyData: any): Track[] => {
    return spotifyData.items.map((item: any) => ({
      image: item.album.images[0]?.url || '', 
      artists: item.artists.map((artist: any) => artist.name).join(', '),
      href: item.external_urls.spotify, 
      name: item.name, 
      preview_url: item.preview_url || '', 
    }));
  };
  

  const handleSearch = async (query: string, source: string) => {
    setIsLoading(true);
    try {
      let response;
      if (source === 'profile') {

        response = await axios.get('http://localhost:9191/spotifySearch', {
          params: { searchQuery: query },
          headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` },
        });
        setResults(mapSpotifyDataToTracks(response.data.tracks)); 
      } else {
        
        if (query === '') {
          setResults([]);
          return;
        }

        response = await axios.get('http://localhost:9191/search', {
          params: { searchQuery: query },
          headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` },
        });
        setResults(response.data.tracks); 
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setIsLoading(false);
    }
  };
  
  


  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch(searchQuery, source || 'default');
    }
  };

  return (
    <Modal opened={isOpen} onClose={onClose} title="Search Spotify" centered size="lg" >
        <Container size={350}>
      <TextInput
        placeholder="Search for a track..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.currentTarget.value)}
        onKeyPress={handleKeyPress}
        leftSection={<IconSearch style={{ width: rem(17), height: rem(17), marginRight: '8px' }} stroke={1.5} />}
        mb="md"
      />
      </Container>
      

      <Grid h={750}>
        {results.map((track) => (
          <Grid.Col span={4} key={track.href}>
            <img src={spotifyLogo} alt="Spotify Logo" style={{ marginLeft: '15px' }} width={70}  />
            <Menu withArrow>
            <Menu.Target>
            <Card shadow="sm" padding="sm" ml={15}  className={classes.card}>
              <AspectRatio ratio={1}>
                <Image w={105} radius="4px" src={track.image} alt={track.name} />
              </AspectRatio>
              <Text className={classes.title}  mt="md" lineClamp={1}>{track.name} </Text>
              <Text className={classes.artist}  size="sm" color="dimmed" lineClamp={1}>
                {track.artists}
              </Text>
            </Card>
            </Menu.Target>
            {!onAlbumSelect ? 
                <CardMenu
                  track={track}
                  artist={track.artists}
                />
              : 
              <Menu.Dropdown>
        <Menu.Item leftSection={<IconPlus size={14} />} onClick={() => handleCardClick(track.image)}>
          Change profile picture
        </Menu.Item>
      </Menu.Dropdown>
              
              }
            </Menu>
          </Grid.Col>
        ))}
      </Grid>

    </Modal>
  );
}

