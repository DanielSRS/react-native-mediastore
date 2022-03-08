import { NativeModules } from 'react-native';

type MediastoreFileType = {
  id: number;
  name: string;
  duration: number;
  size: number;
  mime: string;
  title: string;
  album: string;
  artist: string;
  contentUri: string;
  albumArt: string;
  albumId: string;
};

type Genre = {
  id: string;
  name: string;
  contentUri: string;
};

type MediastoreType = {
  readAudioVideoExternalMedias(): Promise<Array<MediastoreFileType>>;
  readGenreMedias(): Promise<Genre[]>;
};

const { Mediastore } = NativeModules;

export default Mediastore as MediastoreType;
