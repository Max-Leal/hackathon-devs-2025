import { Routes } from '@angular/router';
import { Home } from '../app/pages/home/home';
import { SigninComponent } from './pages/signin/signin';
import { SignupComponent } from './pages/signup/signup';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'home', component: Home },
  { path: 'signin', component: SigninComponent },
  { path: 'signup', component: SignupComponent }
];
