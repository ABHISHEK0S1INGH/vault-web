import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs/internal/operators/filter';
import { NavbarComponent } from './navbar/navbar.component';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, NavbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  showNavbar = false;

  constructor(
    private router: Router,
    private themeService: ThemeService,
  ) {
    // Initialize theme service (loads saved theme from localStorage)
    // This ensures the theme is applied on app startup
    this.themeService.getCurrentTheme();

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        // show navbar if not in Login/Register
        this.showNavbar = !['/login', '/register'].includes(
          event.urlAfterRedirects,
        );
      });
  }
}
