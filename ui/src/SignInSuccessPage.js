import logo from './assets/logo.svg';
import './SignInSuccessPage.css';

import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

export default function SignInSuccessPage() {
  const navigate = useNavigate();
  const { state } = useLocation();
  const { userName } = state;

  return (
    <div className="sign-in-page">
      <header className='App-header'>
        <div className='App-logo'>
          <img src={logo} onClick={() => {navigate('/')}} alt='logo' width='67' height='70' />
        </div>
        <div className='App-name'>Eatin</div>
      </header>
      <div className='welcome'>{"Welcome back, " + userName + "!"}</div>
      <div className='add-prompt'>
        <p>You can always click on <img src={logo} onClick={() => {navigate('/')}} alt='logo'/> to go home.</p>
      </div>
    </div>
  );
}


