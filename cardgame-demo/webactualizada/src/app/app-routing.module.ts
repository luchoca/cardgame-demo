import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { NewGameComponent } from './pages/new-game/new-game.component';
import { ListGameComponent } from './pages/list-game/list-game.component';
import { AuthGuard } from './shared/guard/auth.guard';
import { BoardComponent } from './pages/board/board.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent },
  { path: 'new', component: NewGameComponent, canActivate: [AuthGuard] },
  { path: 'list', component: ListGameComponent, canActivate: [AuthGuard] },
  { path: 'board/:id', component: BoardComponent, canActivate: [AuthGuard] },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  schemas:[
    CUSTOM_ELEMENTS_SCHEMA
  ],
})

export class AppRoutingModule {}