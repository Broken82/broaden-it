package com.broadenit.broadenit.response;

import com.broadenit.broadenit.spotify.body.AudioFeatures;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AudioFeaturesResponse {
    private List<AudioFeatures> audio_features;
}