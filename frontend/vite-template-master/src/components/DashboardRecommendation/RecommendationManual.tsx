import React, { useEffect, useState } from 'react';
import { Box, Stack, Paper, Slider, Text, Container, Button, Flex, Center, MultiSelect, useCombobox, Pill, Combobox, Group, CheckIcon, PillsInput, ScrollArea } from '@mantine/core';
import classes from './RecommendationManual.module.css';
import hero from '../DashboardHome/Hero.module.css';
import axios from 'axios';
import { useDisclosure } from '@mantine/hooks';
import { ManualModal } from './RecommendationManualModal';


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

export function ManualRecommendation() {
  const [acousticness, setAcousticness] = useState(0);
  const [danceability, setDanceability] = useState(0);
  const [valence, setValence] = useState(0);
  const [energy, setEnergy] = useState(0);
  const [instrumentalness, setInstrumentalness] = useState(0);
  const [liveness, setLiveness] = useState(0);
  const [popularity, setPopularity] = useState(0);
  const [speechiness, setSpeechiness] = useState(0);
  const [tempo, setTempo] = useState(30);
  const [mode, setMode] = useState(0);
  const [key, setKey] = useState(0);
  const [timeSignature, setTimeSignature] = useState(3);
  const [search, setSearch] = useState('');
  const [value, setValue] = useState<string[]>([]);
  const [opened, { open, close }] = useDisclosure(false);
  const [isLoading, setIsLoading] = useState(false);  
  const [tracksData, setTracksData] = useState<Track[] | null>(null);

  const [genres, setGenres] = useState<string[]>([]);

  useEffect(() => {
    axios.get('http://localhost:9191/recommendations/getGenres', {
      headers: { 
        Authorization: `Bearer ${localStorage.getItem('jwtToken')}` 
      }
    }).then(response => {
      console.log(response.data);
      setGenres(response.data);
    }).catch(error => {
      console.error(error); 
    });
  }, []);

  const combobox = useCombobox({
    onDropdownClose: () => combobox.resetSelectedOption(),
    onDropdownOpen: () => combobox.updateSelectedOptionIndex('active'),
  });

  const handleValueSelect = (val: string) =>
    setValue((current) =>
      current.includes(val) ? current.filter((v) => v !== val) : [...current, val]
    );

    const handleValueRemove = (val: string) =>
      setValue((current) => current.filter((v) => v !== val));

    const values = value.map((item) => (
      <Pill key={item} withRemoveButton onRemove={() => handleValueRemove(item)}>
        {item}
      </Pill>
    ));

    const options = genres
    .filter((item) => item.toLowerCase().includes(search.trim().toLowerCase()))
    .map((item) => (
      <Combobox.Option value={item} key={item} active={value.includes(item)} disabled={value.length >= 1 && !value.includes(item)}>
        <Group gap="sm">
          {value.includes(item) ? <CheckIcon size={12} /> : null}
          <span>{item}</span>
        </Group>
      </Combobox.Option>
    ));


  const handleButtonClick = () => {
    setIsLoading(true);
    open();
    axios.post<Response>('http://localhost:9191/recommendations/manual', {
      acousticness: acousticness,
      danceability: danceability,
      energy: energy,
      instrumentalness: instrumentalness,
      liveness: liveness,
      key: key,
      mode: mode,
      tempo: tempo,
      popularity: popularity,
      timeSignature: timeSignature,
      speechiness: speechiness,
      valence: valence,
    },
    
     {
      headers: { 
        Authorization: `Bearer ${localStorage.getItem('jwtToken')}` 
      },
      params: { genres: value.join(',') }
    }).then(response => {
      console.log(response.data);
      setTracksData(response.data.tracks);
      
    }).catch(error => {
      console.error(error); 
    })
    .finally(() => {
      setIsLoading(false);
    });
  };

  return (
    <>
    <Stack mt={200} align="center" justify="center">
      <h1 className={hero.title}> Or</h1>
      <div className={hero.wrapper}>
        <Container mt={200} size={700} className={hero.inner}>
          <h1 className={hero.title}>
            Manualy Adjust{' '}
            <Text component="span" variant="gradient" gradient={{ from: '#ac7ce8', to: '#501599' }} inherit>
              Your Recommendations{' '}
            </Text>
          </h1>
          <Text className={hero.description}>
            Use the sliders to adjust the recommendations based on your preferences.
          </Text>
        </Container>
      </div>

      <Stack align="stretch">
        <Paper shadow="xs" radius="xl" p="md" w={1000} withBorder>
          <Flex
          align="center"
          justify="center"
          >
          <Combobox store={combobox} onOptionSubmit={handleValueSelect} withinPortal={false}>
            <Combobox.DropdownTarget>
              <PillsInput onClick={() => combobox.openDropdown()} w={400}>
                <Pill.Group>
                  {values}

                  <Combobox.EventsTarget>
                    <PillsInput.Field
                      onFocus={() => combobox.openDropdown()}
                      onBlur={() => combobox.closeDropdown()}
                      value={search}
                      placeholder="Pick genres"
                      onChange={(event) => {
                        combobox.updateSelectedOptionIndex();
                        setSearch(event.currentTarget.value);
                      }}
                      onKeyDown={(event) => {
                        if (event.key === 'Backspace' && search.length === 0) {
                          event.preventDefault();
                          handleValueRemove(value[value.length - 1]);
                        }
                      }}
                    />
                  </Combobox.EventsTarget>
                </Pill.Group>
              </PillsInput>
            </Combobox.DropdownTarget>
          <Combobox.Dropdown>
        <Combobox.Options>
        <ScrollArea.Autosize mah={200} type="scroll">
          {options.length > 0 ? options : <Combobox.Empty>Nothing found...</Combobox.Empty>}
          </ScrollArea.Autosize>
        </Combobox.Options>
      </Combobox.Dropdown>
    </Combobox>
          </Flex>
          <Text ta="center">Acousticness</Text>
          <Slider className={classes.slider}
            value={acousticness}
            onChange={setAcousticness}
            min={0}
            max={1}
            step={0.1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Danceability</Text>
          <Slider className={classes.slider}
            value={danceability}

            onChange={setDanceability}
            min={0}
            max={1}
            step={0.1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />


          <Text ta="center">Energy</Text>
          <Slider className={classes.slider}
            value={energy}
            onChange={setEnergy}
            min={0}
            max={1}
            step={0.1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Instrumentalness</Text>
          <Slider className={classes.slider}
            value={instrumentalness}
            onChange={setInstrumentalness}
            min={0}
            max={1}
            step={0.1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Liveness</Text>
          <Slider className={classes.slider}
            value={liveness}
            onChange={setLiveness}
            min={0}
            max={1}
            step={0.1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Key</Text>
          <Slider className={classes.slider}
            value={key}
            onChange={setKey}
            min={0}
            max={11}
            step={1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Mode</Text>
          <Slider className={classes.slider}
            value={mode}
            onChange={setMode}
            min={0}
            max={1}
            step={1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Tempo</Text>
          <Slider className={classes.slider}
            value={tempo}
            onChange={setTempo}
            min={30}
            max={200}
            step={1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Popularity</Text>
          <Slider className={classes.slider}
            value={popularity}
            onChange={setPopularity}
            min={0}
            max={100}
            step={1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Time signature</Text>
          <Slider className={classes.slider}
            value={timeSignature}
            onChange={setTimeSignature}
            min={3}
            max={7}
            step={1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />
          

          <Text ta="center">Speechiness</Text>
          <Slider className={classes.slider}
            value={speechiness}
            onChange={setSpeechiness}
            min={0}
            max={1}
            step={0.1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Text ta="center">Valence</Text>
          <Slider className={classes.slider}
            value={valence}
            onChange={setValence}
            min={0}
            max={1}
            step={0.1}
            label={(value) => value.toFixed(1)}
            styles={{ markLabel: { display: 'none' } }}
          />

          <Flex justify="center" mt="md">
            <Button onClick={handleButtonClick}>Send it</Button>
          </Flex>
        </Paper>
      </Stack>
    </Stack>
    <ManualModal isOpen={opened} onClose={close} isLoading={isLoading} tracks={tracksData || []} />
    </>
  );
}
