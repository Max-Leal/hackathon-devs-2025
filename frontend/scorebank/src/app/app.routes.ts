import { Routes } from '@angular/router';
import { Home } from '../app/pages/home/home';
import { Signin } from './pages/signin/signin';
import { Signup } from './pages/signup/signup';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'home', component: Home },
  { path: 'signin', component: Signin },
  { path: 'signup', component: Signup }
];
