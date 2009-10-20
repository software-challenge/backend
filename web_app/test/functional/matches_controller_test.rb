require 'test_helper'

class MatchesControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:matches)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create match" do
    assert_difference('Match.count') do
      post :create, :match => { }
    end

    assert_redirected_to match_path(assigns(:match))
  end

  test "should show match" do
    get :show, :id => matches(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => matches(:one).to_param
    assert_response :success
  end

  test "should update match" do
    put :update, :id => matches(:one).to_param, :match => { }
    assert_redirected_to match_path(assigns(:match))
  end

  test "should destroy match" do
    assert_difference('Match.count', -1) do
      delete :destroy, :id => matches(:one).to_param
    end

    assert_redirected_to matches_path
  end
end
