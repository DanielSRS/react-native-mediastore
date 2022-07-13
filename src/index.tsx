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

type GenreMembers = {
  memberId: number;
  albumId: string;
  artistId: string;
  audioId: string;
  displayName: string;
  genreId: string;
  title: string;
  track: string;
  relativePath: string;
  contentUri: string;
  album: string;
  artist: string;
  composer: string;
  contentDirectory: string;
};

type MediastoreType = {
  readAudioVideoExternalMedias(): Promise<Array<MediastoreFileType>>;
  readGenreMedias(): Promise<Genre[]>;
  readGenreMembers: (genreId: number) => Promise<GenreMembers[]>;
};

const { Mediastore } = NativeModules;

export default Mediastore as MediastoreType;
