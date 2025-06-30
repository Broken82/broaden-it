import { Container, Text, Image } from '@mantine/core';
import classes from './Hero.module.css';

export function Hero() {
  return (
    <div className={classes.wrapper}>
      <Container size={700} className={classes.inner}>
        <h1 className={classes.title}>
          Welcome to{' '}
          <Text component="span" variant="gradient" gradient={{ from: 'indigo', to: 'pink' }} inherit>
            Your Music Hub
          </Text>
        </h1>

        <Text className={classes.description}>
          Discover and enjoy music tailored just for you. Start your journey with personalized recommendations and curated playlists.
        </Text>

        <Image
          src="https://upload.wikimedia.org/wikipedia/commons/6/6e/Colour_soundwave.svg"
          height={200}
          alt="Music related"
          className={classes.heroImage}
        />
      </Container>
    </div>
  );
}
