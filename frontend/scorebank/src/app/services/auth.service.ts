import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API_URL = 'http://localhost:8080/auth'; // ajuste conforme necessário

  constructor(private http: HttpClient, private router: Router) {}

register(data: any): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, data, {
      responseType: 'text' // Backend retorna texto, não JSON
    });
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, data);
  }

  setUserData(user: any): void {
    localStorage.setItem('userId', user.id);
    localStorage.setItem('userName', user.name || user.fullName);
    localStorage.setItem('userEmail', user.email);
  }

  // Recuperar dados do usuário
  getUserData(): any {
    return {
      id: localStorage.getItem('userId'),
      name: localStorage.getItem('userName'),
      email: localStorage.getItem('userEmail')
    };
  }

  // Verificar se está logado
  isLogged(): boolean {
    console.log(localStorage.getItem('userId'))
    //return true;
    return !!localStorage.getItem('userId');
  }

  // Logout
  logout(): void {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
  }
}

