import logo from './assets/logo.svg';
import calendar_icon from './assets/calendar_icon.svg';
import person_icon from './assets/person_icon.svg';
import './App.css';

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import RestaurantCard from './RestaurantCard';
import DatePicker from 'react-datepicker';
import "react-datepicker/dist/react-datepicker.css";

function App() {
  const [startdate, setStartDate] = useState(new Date());
  const [paxNumber, setPaxNumber] = useState(0);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [restaurants, setRestaurants] = useState([]);
  const navigate = useNavigate();

  return (
    <div className='App'>
      <header className='App-header'>
        <div className='App-logo'>
          <img src={logo} alt='logo' width='67' height='70' />
        </div>
        <div className='App-name'>Eatin</div>
        <div className='buttons'>
          <button className='sign-up-button' onClick={() => {navigate('/sign-up-page')}}>Sign up</button>
          <button className='sign-in-button' onClick={() => {navigate('/sign-in-page')}}>Sign in</button>
        </div>
      </header>

      <div className='body'>
        <div className='title'>Your Reservation Assistant</div>
        <div className='selections'>
          <img src={calendar_icon} 
            style={{backgroundColor: 'white', borderRadius: '3px'}} 
            alt='calendar-icon' width='41' height='41' />
          <DatePicker 
            selected={startdate} 
            onChange={(date) => setStartDate(date)} 
            showTimeSelect  
            dateFormat="yyyy/MM/d h:mm aa"
          />
          <img src={person_icon} 
            style={{backgroundColor: 'white', borderRadius: '3px', marginLeft: '50px', padding: '5.5px'}} 
            alt='person-icon' width='30' height='30' />
          <input className='pax-input' type="text" 
            placeholder='How Many Pax?' 
            onChange={(e) => setPaxNumber(e.target.value) } />
          <input className='search-box' type="text" 
            placeholder='What to Eat?'
            onChange={(e) => setSearchKeyword(e.target.value)}
            style={{marginLeft: '50px', borderRadius: '10px', borderWidth: '0'}} />
          <button className='body-search-button' onClick={searchRestaurant()}>Search</button>
        </div>
      </div>
      <div className='restaurants'>
        <RestaurantCard />
        <RestaurantCard />
        <RestaurantCard />
        <RestaurantCard />
        <RestaurantCard />
      </div>
    </div>
  );

  function searchRestaurant() {
    console.log("start date: ", startdate)
    console.log("paxNumber: ", paxNumber)
    console.log("searchKeyword: ", searchKeyword)
    // send get request and res is list of restaurants, set as state
  }
}

export default App;
