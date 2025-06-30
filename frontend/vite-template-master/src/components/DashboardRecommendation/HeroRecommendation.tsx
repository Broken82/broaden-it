import { Container, Text } from '@mantine/core';
import classes from '../DashboardHome/Hero.module.css';

export function HeroRecommendation() {
  return (
    <div className={classes.wrapper}>
      <Container size={700} className={classes.inner}>
        <h1 className={classes.title}>
          Your Personalized{' '}
          <Text component="span" variant="gradient" gradient={{ from: '#ff7e5f', to: '#feb47b' }} inherit>
            Music Recommendations
          </Text>
        </h1>

        <Text className={classes.description}>
          Dive into music tailored just for you. Choose your preferred recommendation method and discover tracks that match your taste.
        </Text>
      </Container>
    </div>
  );
}
