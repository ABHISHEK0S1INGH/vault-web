import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ThemeService } from '../services/theme.service';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {
  isMobileMenuOpen = false;

  constructor(public themeService: ThemeService) {}

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu() {
    this.isMobileMenuOpen = false;
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  get isDarkTheme(): boolean {
    return this.themeService.getCurrentTheme() === 'dark';
  }
}
