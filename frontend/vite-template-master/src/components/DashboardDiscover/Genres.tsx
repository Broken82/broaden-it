import React, { useEffect, useState } from 'react';
import { Box, Grid, Text, Paper, Stack } from '@mantine/core';
import classes from './Genres.module.css';
import headers from './Headers.module.css';
import { useDisclosure } from '@mantine/hooks';
import { GenreModal } from './GenreModal';
import axios from 'axios';

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
const genres = [
  { name: 'Rock', color: '#FF6347', id: 'rock' },
  { name: 'Jazz', color: '#8E44AD', id: 'jazz' },
  { name: 'Hip-Hop', color: '#3498DB', id: 'hip-hop' },
  { name: 'Classical', color: '#D4AC0D', id: 'classical' },
  { name: 'Pop', color: '#FF69B4', id: 'pop' },
  { name: 'Electronic/Dance', color: '#2C3E50', id: 'dance/electronic' },
  { name: 'Metal', color: '#7F8C8D', id: 'metal' },
  { name: 'Alternative', color: '#E74C3C', id: 'alternative' },
  { name: 'Punk', color: '#F39C12', id: 'punk' },
  { name: 'R&B', color: '#9B59B6', id: 'R&B' },
];

function GenreCard({ name, color, onClick }: { name: string, color: string, onClick: () => void }) {
  return (
    <Box 
      style={{
        backgroundColor: color,
        width: 150,
        height: 50,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        margin: 10,
        borderRadius: 8, 
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)', 
      }}
      className={classes.genre}
      onClick={onClick}
    >
      <Text size="lg" fw={700} style={{ color: '#fff', textAlign: 'center', textShadow: '0 1px 2px rgba(0, 0, 0, 0.2)' }}>
        {name}
      </Text>
    </Box>
  );
}

export default function GenresGrid() {
  const [opened, { open, close }] = useDisclosure(false);
  const [playlistData, setPlaylistData] = useState<Track[] | null>(null);
  const [selectedGenreId, setSelectedGenreId] = useState<string | null>(null);
  const [selectedGenreName, setSelectedGenreName] = useState<string | null>(null); 
  const [isLoading, setIsLoading] = useState(false);  

  useEffect(() => {
    if (selectedGenreId) {
      setIsLoading(true);  
      axios.post<Response>('http://localhost:9191/genres', { genreId: selectedGenreId })
        .then((response) => {
          console.log('Fetched playlist data:', response.data.tracks);
          setPlaylistData(response.data.tracks);
        })
        .catch((error) => {
          console.error('Error fetching playlist data:', error);
        })
        .finally(() => {
          setIsLoading(false);  
        });
    }
  }, [selectedGenreId]);

  function handleGenreClick(genreId: string, genreName: string) { 
    setSelectedGenreId(genreId);
    setSelectedGenreName(genreName); 
    setPlaylistData([]); 
    open(); 
  }

  function handleCloseClick(){
    close();
    setSelectedGenreId(null);
    setSelectedGenreName(null);
    setPlaylistData([]);
  }

  return (
    <>
      <Stack>
        <Text className={headers.paperheader}
          pl={30}
          mb={0}
          variant="gradient"
          gradient={{ from: '#800080', to: '#ffc0cb', deg: 360 }}
        >
          Genres
        </Text>

        <Paper shadow="xs" radius="xl" p="md" m="md" mt={0} withBorder>
          <Grid justify="center" align="center">
            {genres.map((genre, index) => (
              <Grid.Col span="auto" key={index}>
                <GenreCard 
                  name={genre.name} 
                  color={genre.color} 
                  onClick={() => handleGenreClick(genre.id, genre.name)} 
                />
              </Grid.Col>
            ))}
          </Grid>
        </Paper>
      </Stack>

      <GenreModal 
        isOpen={opened} 
        onClose={handleCloseClick} 
        playlist={playlistData || []} 
        genreName={selectedGenreName}
        isLoading={isLoading}
      />
    </>
  );
}
