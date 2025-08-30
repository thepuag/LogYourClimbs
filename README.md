# LogYourClimbs - Documentation Summary

## Overview

LogYourClimbs is an Android application designed for rock climbers to track their climbing activities and manage interval training workouts. The app provides a comprehensive logbook system for recording climbing routes along with an integrated countdown timer for structured training sessions. [1](#0-0) [2](#0-1) 

## Key Features

### Authentication System
The app includes a complete user authentication system with login and registration capabilities. Users can create accounts with username and password credentials, which are stored locally in the device's SQLite database. [3](#0-2) [4](#0-3) 

### Route Management (Logbook)
The core functionality allows climbers to log and manage their climbing routes with detailed information including:
- Route name and description
- Difficulty grade (from 4 to 10a+ following European grading system)
- Climbing style (Onsight, Flash, or Redpoint)
- Date of ascent
- Personal notes [5](#0-4) [6](#0-5) 

Users can filter, sort, and search through their climbing history, making it easy to track progress and analyze performance over time. [7](#0-6) 

### Statistics and Analytics
The app provides visual statistics showing the distribution of climbed routes by difficulty grade through horizontal bar charts, helping users understand their climbing performance patterns. [8](#0-7) 

### Interval Training Timer
A dedicated countdown timer feature supports interval training workouts with configurable:
- Work periods (in seconds)
- Rest periods (in seconds) 
- Number of rounds/repetitions
- Visual feedback through color-coded backgrounds (green for work, red for rest) [9](#0-8) [10](#0-9) 

### Data Management
The application includes backup and restore functionality, allowing users to export their climbing data to external files and import previously saved data. [11](#0-10) [12](#0-11) 

## Technical Architecture

### Database Structure
The app uses SQLite for local data storage with three main tables:
- **Users**: Stores authentication credentials
- **Routes**: Contains climbing route records
- **Works**: Manages workout configurations [13](#0-12) 

### Technology Stack
- **Platform**: Android (minimum SDK 24, target SDK 34)
- **Language**: Kotlin
- **UI Framework**: Android Views with ViewBinding
- **Database**: SQLite
- **Charts**: MPAndroidChart library for statistics visualization
- **Architecture**: Three-tier architecture with clear separation of presentation, business logic, and data layers [14](#0-13) [15](#0-14) 

### Permissions
The application requires storage permissions for database backup and restore operations. [16](#0-15) 

## User Interface
The app features a clean, intuitive interface with:
- Login/Registration screens for user authentication
- Main logbook view with RecyclerView for route listings
- Floating action button for quick route addition
- Filtering and sorting controls
- Countdown timer with visual status indicators
- Statistics visualization through charts [17](#0-16) 

## Installation and Setup
LogYourClimbs is a standard Android application that can be built using Gradle. The app works offline and stores all data locally on the device, requiring no internet connection for core functionality. [18](#0-17) 

## Notes

The application is primarily designed in Spanish as evidenced by the string resources, but the core functionality and data models are language-agnostic. The app follows modern Android development practices with Kotlin, ViewBinding, and a well-structured database schema. All user data is isolated by user ID, ensuring privacy and data separation between different users of the same device.
