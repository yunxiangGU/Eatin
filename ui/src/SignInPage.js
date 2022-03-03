import logo from './assets/logo.svg';
import './SignInPage.css';

import axios from 'axios';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function SignInPage() {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  return (
    <div className="sign-in-page">
      <header className='App-header'>
        <div className='App-logo'>
          <img src={logo} onClick={() => {navigate('/')}} alt='logo' width='67' height='70' />
        </div>
        <div className='App-name'>Eatin</div>
      </header>
      <div className='name-label'>User Name</div>
      <input className='name-field' type='text' 
        placeholder='user name'
        onChange={(e) => setUserName(e.target.value)}></input>
      <div className='password-label'>Password</div>
      <input className='password-field' type='password' 
        placeholder='password'
        onChange={(e) => setPassword(e.target.value)}></input>
      <button className='sign-in-button' 
        onClick={() => signIn(userName, password)}>
        Sign In
      </button>
    </div>
  );

  function signIn(userName, password) {    
    var body = {
      username: userName,
      password: password
    };

    axios.post('http://localhost:9000/login', body)
    .then(function (response) {
      if (response.status == 200) {
        navigate('/sign-in-success-page', {
          state: { userName: userName }
        })
      }
    })
    .catch(function (error) {
      console.log("sign in request failed with error: ", error);
    });
  }
}


