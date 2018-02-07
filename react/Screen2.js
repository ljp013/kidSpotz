import React, { Component } from 'react';
import { AppRegistry, SectionList, Button, StyleSheet, Text, View } from 'react-native';

export default class SectionListBasics extends Component {
  render() {
    return (
      <View style={styles.container}>
        <SectionList
          sections={[
            {title: 'Your Last Trip', data: ['Bucknell University']},
            {title: 'Your Saved Trips', data: ['Home', 'Campus Theatre', 'Lewisburg Community Garden', 'Hamleys, NY 205, 8th Block', 'Day Care']},
          ]}
          renderItem={({item}) => <Text style={styles.item}>{item}</Text>}
          renderSectionHeader={({section}) => <Text style={styles.sectionHeader}>{section.title}</Text>}
          keyExtractor={(item, index) => index}
        />
      </View>
    )
  }
}
    const styles = StyleSheet.create({
      container: {
       flex: 1,
       paddingTop: 22,
       backgroundColor: 'powderblue',
      },
      sectionHeader: {
        paddingTop: 2,
        paddingLeft: 10,
        paddingRight: 10,
        paddingBottom: 2,
        fontSize: 14,
        fontWeight: 'bold',
        backgroundColor: 'rgba(247,247,247,1.0)',
      },
      item: {
        padding: 10,
        fontSize: 18,
        height: 44,
      },
    })

// skip this line if using Create React Native App
AppRegistry.registerComponent('kidSpotz', () => SectionListBasics);
