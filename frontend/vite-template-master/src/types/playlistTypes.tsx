

export interface Images {
    url: string;
    height: number;
    width: number;
  }
  
  export interface Album {
    images: Images[];
    name: string;
  }
  
  export interface Artist {
    name: string;
  }
  
  export interface Track {
    name: string;
    href: string;
    album: Album;
    artists: Artist[];
  }
  
  export interface TrackItem {
    track: Track;
  }
  
  export interface Tracks {
    items: TrackItem[];
  }
  
  export interface TrendingResponse {
    tracks: Tracks;
  }
  