import logo from './assets/logo.svg';
import './SignUpPage.css';

import axios from 'axios';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function SignUpPage() {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  return (
    <div className="sign-up-page">
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
      <div className='email-label'>Email</div>
      <input className='email-field' type='text' 
        placeholder='email'
        onChange={(e) => setEmail(e.target.value)}></input>
      <div className='password-label'>Password</div>
      <input className='password-field' type='password' 
        placeholder='password'
        onChange={(e) => setPassword(e.target.value)}></input>
      <button className='sign-up-button' 
        onClick={() => signUp(userName, password, email)}>
        Sign Up
      </button>
    </div>
  );

  function signUp(userName, password, email) {
    var body = {
      username: userName,
      password: password,
      email: email
    };
    
    axios.post('http://localhost:9000/signup', body)
    .then(function (response) {
        if (response.status == 200) {
          navigate('/sign-up-success-page', {
            state: { userName: userName }
          })
        }
    })
    .catch(function (error) {
        console.log(error);
    });
  }
}


