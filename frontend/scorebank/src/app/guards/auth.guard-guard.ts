import { CanActivateFn } from '@angular/router';

import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.auth.isLogged()) {
      console.log("Login feito")
      return true;
    }

    this.router.navigate(['/login']);
    console.log("Login nao efetuado")
    return false;
  }
}