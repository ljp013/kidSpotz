import React, { Component } from 'react';
import { AppRegistry, View, Image } from 'react-native';

export default class App extends Component {
  render() {
    return (
      <View>
        <Image
          style={{width: 300, height: 600}}
          source={require('./Notes.jpg')}
        />
        <Image
          source={{uri: 'Notes.jps'}}
        />
      </View>
    );
  }
}

// skip this line if using Create React Native App
AppRegistry.registerComponent('App', () => App);
