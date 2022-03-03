import logo from './assets/logo.svg';
import './SignUpSuccessPage.css';

import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

export default function SignUpSuccessPage() {
  const navigate = useNavigate();
  const { state } = useLocation();
  const { userName } = state;

  return (
    <div className="sign-up-page">
      <header className='App-header'>
        <div className='App-logo'>
          <img src={logo} onClick={() => {navigate('/')}} alt='logo' width='67' height='70' />
        </div>
        <div className='App-name'>Eatin</div>
      </header>
      <div className='welcome'>{"Welcome, " + userName + "!"}</div>
      <div className='add-prompt'>{"You can add your own business here."}</div>
      <div className='add-prompt'>
      <p>You can always click on <img src={logo} onClick={() => {navigate('/')}} alt='logo'/> to go home.</p>
      </div>
    </div>
  );
}


