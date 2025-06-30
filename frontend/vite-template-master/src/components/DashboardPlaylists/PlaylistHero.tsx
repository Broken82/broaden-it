import React from 'react';
import { Container, Text } from '@mantine/core';
import hero from '../DashboardHome/Hero.module.css';

export function PlaylistHero(){
    return(
        <>
            <div className={hero.wrapper}>
      <Container size={700} className={hero.inner}>
        <h1 className={hero.title}>
          Your{' '}
          <Text component="span" variant="gradient" gradient={{ from: '#00C9FF', to: '#92FE9D' }} inherit>
            Curated Playlists
          </Text>
        </h1>

        <Text className={hero.description}>
        Create and explore your personal playlists, filled with tracks from your recommendations and discoveries.
        </Text>
      </Container>
    </div>        
        
        </>


    )
}