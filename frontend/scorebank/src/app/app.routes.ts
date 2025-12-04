import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { SigninComponent } from './pages/signin/signin';
import { SignupComponent } from './pages/signup/signup';
import { DashboardComponent } from './pages/dashboard/dashboard';
import { SimulationComponent } from './pages/simulation/simulation';
import { AuthGuard } from './guards/auth.guard-guard';

export const routes: Routes = [
  { path: '', component: Home, pathMatch: 'full' },
  { path: 'home', component: Home },
  { path: 'signin', component: SigninComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'simulation', component: SimulationComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] }, // rota protegida
  //{ path: 'dashboard', component: DashboardComponent },
  { path: '**', redirectTo: '' }
];
