import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home {
  constructor(private router: Router){}
  onSignIn() {
    this.router.navigate(['/signin']); 
  }

  onSignUp() {
    this.router.navigate(['/signup']); 
  }
}
