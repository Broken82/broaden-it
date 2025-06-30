import { Container, Text, Image } from '@mantine/core';
import classes from '../DashboardHome/Hero.module.css';

export function HeroDiscover() {
  return (
    <div className={classes.wrapper}>
      <Container size={700} className={classes.inner}>
        <h1 className={classes.title}>
          Discover{' '}
          <Text component="span" variant="gradient" gradient={{ from: '#ac7ce8', to: '#501599' }} inherit>
            New Music
          </Text>
        </h1>

        <Text className={classes.description}>
        Explore trending tracks and fresh releases across genres. Find new favorites and add them to your playlists.
        </Text>

      </Container>
    </div>
  );
}
