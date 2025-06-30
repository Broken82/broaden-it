import { useState, useEffect } from 'react';
import { Group, Code, AppShell, Text } from '@mantine/core';
import {
  IconHome,
  IconPresentation,
  IconPlaylist,
  Icon2fa,
  IconBrandSafari,
  IconLogout,
  IconUserCircle,
} from '@tabler/icons-react';
import { MantineLogo } from '@mantinex/mantine-logo';
import { useNavigate } from 'react-router-dom';
import vinylLogo from '../../svg/Disque_Vinyl.svg';
import classes from './Navbar.module.css';

const data = [
  { link: '/dashboard', label: 'Home', icon: IconHome },
  { link: '/dashboard/discover', label: 'Discover', icon: IconBrandSafari },
  { link: '/dashboard/recommendations', label: 'Recommendations', icon: IconPresentation },
  { link: '/dashboard/playlists', label: 'Playlists', icon: IconPlaylist },
  { link: '/dashboard/profile', label: 'Profile', icon: IconUserCircle },
];

export function Navbar() {
  const [active, setActive] = useState('Home');
  const navigate = useNavigate();


  const handleLogout = (event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    event.preventDefault();
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('activeTab');
    navigate('/');
  };

  const links = data.map((item) => (
    <a
      className={classes.link}
      data-active={item.label === active || undefined}
      key={item.label}
      onClick={(event) => {
        event.preventDefault();
        setActive(item.label);
        navigate(item.link);
      }}
    >
      <item.icon className={classes.linkIcon} stroke={1.5} />
      <span>{item.label}</span>
    </a>
  ));

  return (
    <nav className={classes.navbar}>
      <div className={classes.navbarMain}>
        <Group className={classes.header} >
        <img src={vinylLogo} alt="Vinyl Logo" width={28} height={28} />
        <Text className={classes.brand} size='lg' fw={500} ta='left'>Broaden It</Text>
        </Group>
        {links}
      </div>

      <div className={classes.footer}>
        <a href="#" className={classes.link} onClick={handleLogout}>
          <IconLogout className={classes.linkIcon} stroke={1.5} />
          <span>Logout</span>
        </a>
      </div>
    </nav>
  );
}
