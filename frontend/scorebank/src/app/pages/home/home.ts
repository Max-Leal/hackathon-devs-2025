import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
  imports: [RouterLink]
})
export class Home implements OnInit {

  constructor(private router: Router){}

   isLogged = false;

  ngOnInit(): void {
    const userId = localStorage.getItem('userId');
    if (userId) this.isLogged = true;
  }

  onSignIn() {
    this.router.navigate(['/signin']); 
  }

  onSignUp() {
    this.router.navigate(['/signup']); 
  }

  logout(): void {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    window.location.reload();
  }
}
